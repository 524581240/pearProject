package com.geosaas.domain.brand;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    List<Brand> findByTenantIdOrderByIdDesc(Long tenantId);
    Optional<Brand> findByIdAndTenantId(Long id, Long tenantId);
    long countByTenantIdAndStatus(Long tenantId, String status);
}
