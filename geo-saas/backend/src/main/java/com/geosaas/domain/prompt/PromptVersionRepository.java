package com.geosaas.domain.prompt;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PromptVersionRepository extends JpaRepository<PromptVersion, Long> {
    List<PromptVersion> findByPromptIdOrderByVersionNoDesc(Long promptId);
    Optional<PromptVersion> findByPromptIdAndVersionNo(Long promptId, Integer versionNo);
}
