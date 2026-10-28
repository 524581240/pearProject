package com.geosaas.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.geosaas.common.BizException;
import com.geosaas.domain.brand.Brand;
import com.geosaas.domain.brand.BrandRepository;
import com.geosaas.domain.brand.Competitor;
import com.geosaas.domain.brand.CompetitorRepository;
import com.geosaas.domain.job.MonitorJob;
import com.geosaas.domain.job.MonitorJobRepository;
import com.geosaas.domain.job.RunBatch;
import com.geosaas.domain.job.RunBatchRepository;
import com.geosaas.domain.job.RunItem;
import com.geosaas.domain.job.RunItemRepository;
import com.geosaas.domain.prompt.Prompt;
import com.geosaas.domain.prompt.PromptRepository;
import com.geosaas.engine.AiAskRequest;
import com.geosaas.engine.AiAskResult;
import com.geosaas.engine.EngineRouter;
import com.geosaas.security.SecurityUtils;
import com.geosaas.web.dto.JobDtos;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonitorJobService {
    private final MonitorJobRepository monitorJobRepository;
    private final RunBatchRepository runBatchRepository;
    private final RunItemRepository runItemRepository;
    private final PromptRepository promptRepository;
    private final BrandRepository brandRepository;
    private final CompetitorRepository competitorRepository;
    private final BrandService brandService;
    private final QuotaService quotaService;
    private final EngineRouter engineRouter;
    private final ObjectMapper objectMapper;

    public List<MonitorJob> list() {
        return monitorJobRepository.findByTenantIdOrderByIdDesc(SecurityUtils.tenantId());
    }

    @Transactional
    public MonitorJob create(JobDtos.UpsertRequest req) {
        Long tenantId = SecurityUtils.tenantId();
        brandService.getOwned(req.getBrandId());
        List<String> engines = split(req.getEngineCodes());
        quotaService.checkEngineQuota(tenantId, engines.size());
        if (engines.isEmpty()) throw new BizException("至少选择一个引擎");
        if (split(req.getPromptIds()).isEmpty()) throw new BizException("至少选择一个 Prompt");

        MonitorJob job = new MonitorJob();
        job.setTenantId(tenantId);
        job.setBrandId(req.getBrandId());
        job.setName(req.getName());
        job.setFrequency(req.getFrequency() == null ? "DAILY" : req.getFrequency());
        job.setEngineCodes(String.join(",", engines));
        job.setPromptIds(req.getPromptIds());
        job.setRepeatCount(req.getRepeatCount() == null ? 1 : req.getRepeatCount());
        job.setStatus("ACTIVE");
        job.setNextRunAt(LocalDateTime.now().plusMinutes(1));
        return monitorJobRepository.save(job);
    }

    @Transactional
    public RunBatch runNow(Long jobId) {
        MonitorJob job = monitorJobRepository.findByIdAndTenantId(jobId, SecurityUtils.tenantId())
                .orElseThrow(() -> new BizException("任务不存在"));
        return executeJob(job, "MANUAL");
    }

    /** One-shot verify without saving a recurring job */
    @Transactional
    public RunBatch verifyOnce(JobDtos.VerifyRequest req) {
        Long tenantId = SecurityUtils.tenantId();
        Brand brand = brandService.getOwned(req.getBrandId());
        List<String> engines = split(req.getEngineCodes());
        quotaService.checkEngineQuota(tenantId, engines.size());

        MonitorJob ephemeral = new MonitorJob();
        ephemeral.setTenantId(tenantId);
        ephemeral.setBrandId(brand.getId());
        ephemeral.setName("即时验证-" + System.currentTimeMillis());
        ephemeral.setFrequency("MANUAL");
        ephemeral.setEngineCodes(String.join(",", engines));
        ephemeral.setPromptIds(req.getPromptIds());
        ephemeral.setRepeatCount(req.getRepeatCount() == null ? 1 : req.getRepeatCount());
        ephemeral.setStatus("ACTIVE");
        monitorJobRepository.save(ephemeral);
        return executeJob(ephemeral, "MANUAL");
    }

    @Transactional
    public RunBatch executeJob(MonitorJob job, String triggerType) {
        Brand brand = brandRepository.findByIdAndTenantId(job.getBrandId(), job.getTenantId())
                .orElseThrow(() -> new BizException("品牌不存在"));
        List<Long> promptIds = split(job.getPromptIds()).stream().map(Long::valueOf).toList();
        List<String> engines = split(job.getEngineCodes());
        int planned = promptIds.size() * engines.size() * Math.max(1, job.getRepeatCount());
        quotaService.consumeResponses(job.getTenantId(), planned);

        List<Competitor> competitors = competitorRepository.findByBrandIdAndTenantId(brand.getId(), job.getTenantId());
        String competitorNames = competitors.stream().map(Competitor::getName).collect(Collectors.joining(","));

        RunBatch batch = new RunBatch();
        batch.setTenantId(job.getTenantId());
        batch.setJobId(job.getId());
        batch.setTriggerType(triggerType);
        batch.setStatus("RUNNING");
        batch.setTotalItems(planned);
        runBatchRepository.save(batch);

        int success = 0;
        int failed = 0;
        for (Long promptId : promptIds) {
            Prompt prompt = promptRepository.findByIdAndTenantId(promptId, job.getTenantId())
                    .orElseThrow(() -> new BizException("Prompt 不存在: " + promptId));
            for (String engine : engines) {
                for (int r = 0; r < Math.max(1, job.getRepeatCount()); r++) {
                    RunItem item = new RunItem();
                    item.setTenantId(job.getTenantId());
                    item.setBatchId(batch.getId());
                    item.setPromptId(prompt.getId());
                    item.setPromptVersion(prompt.getCurrentVersion());
                    item.setEngineCode(engine);
                    try {
                        AiAskResult result = engineRouter.ask(engine, AiAskRequest.builder()
                                .prompt(prompt.getContent())
                                .brandName(brand.getName())
                                .brandAliases(brand.getAliases())
                                .competitors(competitorNames)
                                .locale(prompt.getLocale())
                                .country(prompt.getCountry())
                                .build());
                        item.setStatus("SUCCESS");
                        item.setRawAnswer(result.getRawAnswer());
                        item.setMentioned(result.isMentioned());
                        item.setPositionRank(result.getPositionRank());
                        item.setSentimentScore(result.getSentimentScore());
                        item.setCitationJson(writeJson(result.getCitations()));
                        item.setCompetitorMentionsJson(writeJson(result.getCompetitorMentions()));
                        item.setLatencyMs(result.getLatencyMs());
                        success++;
                    } catch (Exception e) {
                        item.setStatus("FAILED");
                        item.setErrorMessage(e.getMessage());
                        failed++;
                    }
                    runItemRepository.save(item);
                }
            }
        }

        batch.setSuccessItems(success);
        batch.setFailedItems(failed);
        batch.setStatus(failed == 0 ? "SUCCESS" : (success == 0 ? "FAILED" : "PARTIAL"));
        batch.setFinishedAt(LocalDateTime.now());
        runBatchRepository.save(batch);

        job.setLastRunAt(LocalDateTime.now());
        job.setNextRunAt(nextRun(job.getFrequency()));
        monitorJobRepository.save(job);
        return batch;
    }

    public List<RunBatch> listBatches() {
        return runBatchRepository.findByTenantIdOrderByIdDesc(SecurityUtils.tenantId());
    }

    public JobDtos.BatchDetail batchDetail(Long batchId) {
        RunBatch batch = runBatchRepository.findByIdAndTenantId(batchId, SecurityUtils.tenantId())
                .orElseThrow(() -> new BizException("批次不存在"));
        List<RunItem> items = runItemRepository.findByBatchIdOrderByIdAsc(batchId);
        JobDtos.BatchDetail detail = new JobDtos.BatchDetail();
        detail.setBatch(batch);
        detail.setItems(items);
        return detail;
    }

    public List<MonitorJob> dueJobs(LocalDateTime now) {
        return monitorJobRepository.findByStatusAndNextRunAtBefore("ACTIVE", now).stream()
                .filter(j -> !"MANUAL".equals(j.getFrequency()))
                .toList();
    }

    private LocalDateTime nextRun(String frequency) {
        if ("WEEKLY".equalsIgnoreCase(frequency)) {
            return LocalDateTime.now().plusDays(7);
        }
        if ("MANUAL".equalsIgnoreCase(frequency)) {
            return null;
        }
        return LocalDateTime.now().plusDays(1);
    }

    private List<String> split(String raw) {
        if (raw == null || raw.isBlank()) return new ArrayList<>();
        return Arrays.stream(raw.split("[,，]")).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
    }

    private String writeJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }
}
