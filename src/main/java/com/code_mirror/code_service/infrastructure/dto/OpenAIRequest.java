package com.code_mirror.code_service.infrastructure.dto;

import java.util.List;
import java.util.Map;

public class OpenAIRequest {

    private String model;
    private List<Map<String, String>> messages;
    private double temperature = 0.3;

    public OpenAIRequest(String model, String prompt) {
        this.model = model;
        this.messages = List.of(
                Map.of("role", "system", "content", "Eres un asistente experto en evaluación de código."),
                Map.of("role", "user", "content", prompt)
        );
    }

    public String getModel() {
        return model;
    }

    public List<Map<String, String>> getMessages() {
        return messages;
    }

    public double getTemperature() {
        return temperature;
    }
}

