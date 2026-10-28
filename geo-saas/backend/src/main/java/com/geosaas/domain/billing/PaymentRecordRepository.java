package com.geosaas.domain.billing;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRecordRepository extends JpaRepository<PaymentRecord, Long> {
    List<PaymentRecord> findByTenantIdOrderByIdDesc(Long tenantId);
}
