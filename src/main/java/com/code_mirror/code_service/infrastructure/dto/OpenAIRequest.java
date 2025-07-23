package com.code_mirror.code_service.infrastructure.dto;

import java.util.List;
import java.util.Map;

public class OpenAIRequest {
    private String model;
    private List<Map<String, String>> messages;
    private double temperature = 0.9;

    public OpenAIRequest(String model, String prompt) {
        this.model = model;
        this.messages = List.of(Map.of("role", "user", "content", prompt));
    }

    public String getModel() {
        return model;
    }

    public List<Map<String, String>> getMessages() {
        return messages;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setMessages(List<Map<String, String>> messages) {
        this.messages = messages;
    }
}
