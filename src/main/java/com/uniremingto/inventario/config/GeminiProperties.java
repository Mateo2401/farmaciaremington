package com.uniremingto.inventario.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "farmacia-ai.gemini")
public record GeminiProperties(
        String apiKey,
        String model,
        Duration connectTimeout,
        Duration readTimeout) {

    public static final String DEFAULT_MODEL = "gemini-2.5-flash";

    public GeminiProperties {
        if (model == null || model.isBlank()) {
            model = DEFAULT_MODEL;
        }
    }
}
