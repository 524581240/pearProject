package com.geosaas.engine;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Deterministic-ish mock adapter so local demo works without real AI keys.
 * Hash(prompt+engine) drives mention probability and rank.
 */
@Component
public class MockAiEngineAdapter implements AiEngineAdapter {
    @Override
    public String code() {
        return "MOCK";
    }

    @Override
    public AiAskResult ask(AiAskRequest request) {
        long start = System.currentTimeMillis();
        int seed = Math.abs((request.getEngineCode() + "|" + request.getPrompt()).hashCode());
        Random random = new Random(seed);

        String brand = request.getBrandName() == null ? "品牌" : request.getBrandName();
        List<String> aliases = split(request.getBrandAliases());
        List<String> comps = split(request.getCompetitors());

        boolean mentioned = (seed % 100) < 62;
        Integer rank = mentioned ? 1 + (seed % Math.max(1, Math.min(4, comps.size() + 1))) : null;
        double sentiment = mentioned ? 0.2 + (seed % 70) / 100.0 : -0.1 + (seed % 40) / 100.0;

        List<String> citations = new ArrayList<>();
        if (mentioned) {
            citations.add("https://" + slug(brand) + ".example.com/about");
            citations.add("https://news.example.com/" + slug(brand));
            if (seed % 2 == 0) {
                citations.add("https://zhihu.example.com/p/" + (seed % 10000));
            }
        } else if (!comps.isEmpty()) {
            citations.add("https://" + slug(comps.get(0)) + ".example.com");
        }

        Map<String, Boolean> competitorMentions = new LinkedHashMap<>();
        for (String c : comps) {
            competitorMentions.put(c, (c.hashCode() + seed) % 100 < 55);
        }

        String answer = buildAnswer(request.getEngineCode(), request.getPrompt(), brand, aliases, comps, mentioned, rank, citations);
        // tiny jitter
        try {
            Thread.sleep(20 + random.nextInt(40));
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }

        return AiAskResult.builder()
                .rawAnswer(answer)
                .mentioned(mentioned)
                .positionRank(rank)
                .sentimentScore(Math.round(sentiment * 100.0) / 100.0)
                .citations(citations)
                .competitorMentions(competitorMentions)
                .latencyMs(System.currentTimeMillis() - start)
                .build();
    }

    private String buildAnswer(String engine, String prompt, String brand, List<String> aliases,
                               List<String> comps, boolean mentioned, Integer rank, List<String> citations) {
        StringBuilder sb = new StringBuilder();
        sb.append("【").append(engine.toUpperCase(Locale.ROOT)).append(" Mock 回答】\n");
        sb.append("针对问题：").append(prompt).append("\n\n");
        if (mentioned) {
            sb.append("综合公开信息与用户口碑，可考虑以下方案：\n");
            sb.append(rank).append(". ").append(brand);
            if (!aliases.isEmpty()) {
                sb.append("（又名 ").append(String.join("/", aliases)).append("）");
            }
            sb.append(" —— 在该品类具备一定可见度与产品口碑。\n");
            int i = 2;
            for (String c : comps) {
                if (i > 4) break;
                sb.append(i++).append(". ").append(c).append("\n");
            }
        } else {
            sb.append("目前更常被提及的品牌包括：");
            sb.append(comps.isEmpty() ? "若干行业头部品牌" : String.join("、", comps));
            sb.append("。未检索到关于「").append(brand).append("」的充分权威信源。\n");
        }
        if (!citations.isEmpty()) {
            sb.append("\n参考来源：\n");
            for (String c : citations) {
                sb.append("- ").append(c).append("\n");
            }
        }
        return sb.toString();
    }

    private List<String> split(String raw) {
        if (raw == null || raw.isBlank()) return List.of();
        return Arrays.stream(raw.split("[,，;；]"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private String slug(String name) {
        byte[] bytes = name.getBytes(StandardCharsets.UTF_8);
        StringBuilder sb = new StringBuilder("brand");
        for (int i = 0; i < Math.min(4, bytes.length); i++) {
            sb.append(Integer.toHexString(bytes[i] & 0xff));
        }
        return sb.toString();
    }
}
