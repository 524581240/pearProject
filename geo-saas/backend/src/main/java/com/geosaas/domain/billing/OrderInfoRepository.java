package com.geosaas.domain.billing;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderInfoRepository extends JpaRepository<OrderInfo, Long> {
    Optional<OrderInfo> findByOrderNo(String orderNo);
    List<OrderInfo> findByTenantIdOrderByIdDesc(Long tenantId);
    Optional<OrderInfo> findByIdAndTenantId(Long id, Long tenantId);
}
