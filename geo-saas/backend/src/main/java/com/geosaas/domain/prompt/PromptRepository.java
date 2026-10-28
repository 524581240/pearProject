package com.geosaas.domain.prompt;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PromptRepository extends JpaRepository<Prompt, Long> {
    List<Prompt> findByTenantIdAndBrandIdOrderByIdDesc(Long tenantId, Long brandId);
    List<Prompt> findByTenantIdOrderByIdDesc(Long tenantId);
    Optional<Prompt> findByIdAndTenantId(Long id, Long tenantId);
    long countByTenantIdAndStatus(Long tenantId, String status);
}
