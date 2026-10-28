package com.geosaas.domain.job;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MonitorJobRepository extends JpaRepository<MonitorJob, Long> {
    List<MonitorJob> findByTenantIdOrderByIdDesc(Long tenantId);
    Optional<MonitorJob> findByIdAndTenantId(Long id, Long tenantId);
    List<MonitorJob> findByStatusAndNextRunAtBefore(String status, LocalDateTime time);
}
