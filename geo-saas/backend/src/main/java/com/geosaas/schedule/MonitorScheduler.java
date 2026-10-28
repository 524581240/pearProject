package com.geosaas.schedule;

import com.geosaas.domain.job.MonitorJob;
import com.geosaas.service.MonitorJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "geosaas.scheduler", name = "enabled", havingValue = "true", matchIfMissing = true)
public class MonitorScheduler {
    private final MonitorJobService monitorJobService;

    @Scheduled(cron = "${geosaas.scheduler.cron:0 */5 * * * *}")
    public void tick() {
        List<MonitorJob> due = monitorJobService.dueJobs(LocalDateTime.now());
        for (MonitorJob job : due) {
            try {
                log.info("Scheduled run jobId={} tenantId={}", job.getId(), job.getTenantId());
                monitorJobService.executeJob(job, "SCHEDULE");
            } catch (Exception e) {
                log.warn("Scheduled job failed id={}: {}", job.getId(), e.getMessage());
            }
        }
    }
}
