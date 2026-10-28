package com.geosaas.engine;

public interface AiEngineAdapter {
    String code();
    AiAskResult ask(AiAskRequest request);
}
