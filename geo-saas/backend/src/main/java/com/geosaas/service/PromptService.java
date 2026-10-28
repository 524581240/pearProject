package com.geosaas.service;

import com.geosaas.common.BizException;
import com.geosaas.domain.prompt.Prompt;
import com.geosaas.domain.prompt.PromptRepository;
import com.geosaas.domain.prompt.PromptVersion;
import com.geosaas.domain.prompt.PromptVersionRepository;
import com.geosaas.security.SecurityUtils;
import com.geosaas.web.dto.PromptDtos;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PromptService {
    private final PromptRepository promptRepository;
    private final PromptVersionRepository promptVersionRepository;
    private final BrandService brandService;
    private final QuotaService quotaService;

    public List<Prompt> list(Long brandId) {
        Long tenantId = SecurityUtils.tenantId();
        if (brandId != null) {
            brandService.getOwned(brandId);
            return promptRepository.findByTenantIdAndBrandIdOrderByIdDesc(tenantId, brandId);
        }
        return promptRepository.findByTenantIdOrderByIdDesc(tenantId);
    }

    @Transactional
    public Prompt create(PromptDtos.UpsertRequest req) {
        Long tenantId = SecurityUtils.tenantId();
        brandService.getOwned(req.getBrandId());
        quotaService.checkPromptQuota(tenantId);

        Prompt prompt = new Prompt();
        prompt.setTenantId(tenantId);
        prompt.setBrandId(req.getBrandId());
        prompt.setGroupName(req.getGroupName() == null ? "默认" : req.getGroupName());
        prompt.setContent(req.getContent());
        prompt.setCurrentVersion(1);
        prompt.setLocale(req.getLocale() == null ? "zh-CN" : req.getLocale());
        prompt.setCountry(req.getCountry() == null ? "CN" : req.getCountry());
        prompt.setTags(req.getTags());
        prompt.setStatus("ACTIVE");
        promptRepository.save(prompt);

        PromptVersion v = new PromptVersion();
        v.setTenantId(tenantId);
        v.setPromptId(prompt.getId());
        v.setVersionNo(1);
        v.setContent(req.getContent());
        v.setChangeNote("初始版本");
        promptVersionRepository.save(v);
        return prompt;
    }

    @Transactional
    public Prompt update(Long id, PromptDtos.UpsertRequest req) {
        Prompt prompt = getOwned(id);
        boolean contentChanged = req.getContent() != null && !req.getContent().equals(prompt.getContent());
        if (req.getGroupName() != null) prompt.setGroupName(req.getGroupName());
        if (req.getTags() != null) prompt.setTags(req.getTags());
        if (req.getLocale() != null) prompt.setLocale(req.getLocale());
        if (req.getCountry() != null) prompt.setCountry(req.getCountry());
        if (contentChanged) {
            int next = prompt.getCurrentVersion() + 1;
            prompt.setContent(req.getContent());
            prompt.setCurrentVersion(next);
            PromptVersion v = new PromptVersion();
            v.setTenantId(prompt.getTenantId());
            v.setPromptId(prompt.getId());
            v.setVersionNo(next);
            v.setContent(req.getContent());
            v.setChangeNote(req.getChangeNote() == null ? "内容更新" : req.getChangeNote());
            promptVersionRepository.save(v);
        }
        return promptRepository.save(prompt);
    }

    public List<PromptVersion> versions(Long id) {
        getOwned(id);
        return promptVersionRepository.findByPromptIdOrderByVersionNoDesc(id);
    }

    @Transactional
    public void archive(Long id) {
        Prompt prompt = getOwned(id);
        prompt.setStatus("ARCHIVED");
        promptRepository.save(prompt);
    }

    public Prompt getOwned(Long id) {
        return promptRepository.findByIdAndTenantId(id, SecurityUtils.tenantId())
                .orElseThrow(() -> new BizException("Prompt 不存在"));
    }
}
