package com.geosaas.domain.brand;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompetitorRepository extends JpaRepository<Competitor, Long> {
    List<Competitor> findByBrandIdAndTenantId(Long brandId, Long tenantId);
}
