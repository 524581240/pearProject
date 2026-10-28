package com.geosaas.domain.job;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RunItemRepository extends JpaRepository<RunItem, Long> {
    List<RunItem> findByBatchIdOrderByIdAsc(Long batchId);
    List<RunItem> findByTenantIdOrderByIdDesc(Long tenantId);
    List<RunItem> findByTenantIdAndStatusOrderByIdDesc(Long tenantId, String status);
}
