package com.geosaas.domain.engine;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EngineProviderRepository extends JpaRepository<EngineProvider, Long> {
    Optional<EngineProvider> findByCode(String code);
    List<EngineProvider> findByEnabledTrueOrderByIdAsc();
}
