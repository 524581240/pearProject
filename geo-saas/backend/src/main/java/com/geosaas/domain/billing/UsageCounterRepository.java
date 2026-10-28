package com.geosaas.domain.billing;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsageCounterRepository extends JpaRepository<UsageCounter, Long> {
    Optional<UsageCounter> findByTenantIdAndYearMonth(Long tenantId, String yearMonth);
}
