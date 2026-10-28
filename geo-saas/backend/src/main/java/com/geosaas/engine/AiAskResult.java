package com.geosaas.engine;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class AiAskResult {
    private String rawAnswer;
    private boolean mentioned;
    private Integer positionRank;
    private double sentimentScore;
    private List<String> citations;
    private Map<String, Boolean> competitorMentions;
    private long latencyMs;
}
