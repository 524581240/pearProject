package com.geosaas.domain.job;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RunBatchRepository extends JpaRepository<RunBatch, Long> {
    List<RunBatch> findByTenantIdOrderByIdDesc(Long tenantId);
    Optional<RunBatch> findByIdAndTenantId(Long id, Long tenantId);
    List<RunBatch> findByJobIdOrderByIdDesc(Long jobId);
}
