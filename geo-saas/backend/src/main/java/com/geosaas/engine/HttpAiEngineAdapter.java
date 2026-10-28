package com.geosaas.engine;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.geosaas.common.BizException;
import com.geosaas.domain.engine.EngineProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Generic OpenAI-compatible chat completions adapter.
 * Used when geosaas.engine.mode=real and provider has endpoint + apiKey.
 */
@Component
@RequiredArgsConstructor
public class HttpAiEngineAdapter {
    private final ObjectMapper objectMapper;
    private final MockAiEngineAdapter fallback;

    @Value("${geosaas.engine.mode:mock}")
    private String mode;

    public AiAskResult ask(EngineProvider provider, AiAskRequest request) {
        if (!"real".equalsIgnoreCase(mode)
                || provider.getEndpoint() == null || provider.getEndpoint().isBlank()
                || provider.getApiKey() == null || provider.getApiKey().isBlank()) {
            AiAskRequest mockReq = AiAskRequest.builder()
                    .engineCode(provider.getCode())
                    .prompt(request.getPrompt())
                    .brandName(request.getBrandName())
                    .brandAliases(request.getBrandAliases())
                    .competitors(request.getCompetitors())
                    .locale(request.getLocale())
                    .country(request.getCountry())
                    .build();
            return fallback.ask(mockReq);
        }

        long start = System.currentTimeMillis();
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("model", "gpt-4o-mini");
            body.put("messages", List.of(
                    Map.of("role", "system", "content", "你是搜索助手，请给出简洁、可引用的品牌推荐回答。"),
                    Map.of("role", "user", "content", request.getPrompt())
            ));

            String raw = RestClient.create()
                    .post()
                    .uri(provider.getEndpoint())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + provider.getApiKey())
                    .body(body)
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(raw);
            String content = root.path("choices").path(0).path("message").path("content").asText("");
            return parseAnswer(content, request, System.currentTimeMillis() - start);
        } catch (Exception e) {
            throw new BizException("引擎调用失败[" + provider.getCode() + "]: " + e.getMessage());
        }
    }

    private AiAskResult parseAnswer(String content, AiAskRequest request, long latency) {
        String lower = content.toLowerCase(Locale.ROOT);
        String brand = request.getBrandName() == null ? "" : request.getBrandName();
        boolean mentioned = !brand.isBlank() && (content.contains(brand) || lower.contains(brand.toLowerCase(Locale.ROOT)));
        if (!mentioned && request.getBrandAliases() != null) {
            for (String a : request.getBrandAliases().split("[,，]")) {
                if (!a.isBlank() && content.contains(a.trim())) {
                    mentioned = true;
                    break;
                }
            }
        }
        Integer rank = mentioned ? 1 : null;
        List<String> citations = new ArrayList<>();
        for (String line : content.split("\n")) {
            String t = line.trim();
            if (t.startsWith("http://") || t.startsWith("https://")) {
                citations.add(t.split("\\s+")[0]);
            }
        }
        Map<String, Boolean> comps = new HashMap<>();
        if (request.getCompetitors() != null) {
            for (String c : request.getCompetitors().split("[,，]")) {
                String name = c.trim();
                if (!name.isEmpty()) {
                    comps.put(name, content.contains(name));
                }
            }
        }
        return AiAskResult.builder()
                .rawAnswer(content)
                .mentioned(mentioned)
                .positionRank(rank)
                .sentimentScore(mentioned ? 0.6 : 0.0)
                .citations(citations)
                .competitorMentions(comps)
                .latencyMs(latency)
                .build();
    }
}
