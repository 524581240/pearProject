package com.geosaas.engine;

import com.geosaas.common.BizException;
import com.geosaas.domain.engine.EngineProvider;
import com.geosaas.domain.engine.EngineProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EngineRouter {
    private final EngineProviderRepository engineProviderRepository;
    private final HttpAiEngineAdapter httpAiEngineAdapter;

    public AiAskResult ask(String engineCode, AiAskRequest request) {
        EngineProvider provider = engineProviderRepository.findByCode(engineCode)
                .orElseThrow(() -> new BizException("未知引擎: " + engineCode));
        if (!Boolean.TRUE.equals(provider.getEnabled())) {
            throw new BizException("引擎已禁用: " + engineCode);
        }
        AiAskRequest enriched = AiAskRequest.builder()
                .engineCode(engineCode)
                .prompt(request.getPrompt())
                .brandName(request.getBrandName())
                .brandAliases(request.getBrandAliases())
                .competitors(request.getCompetitors())
                .locale(request.getLocale())
                .country(request.getCountry())
                .build();
        return httpAiEngineAdapter.ask(provider, enriched);
    }
}
