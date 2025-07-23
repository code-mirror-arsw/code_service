package com.code_mirror.code_service.infrastruture.dto;

import com.code_mirror.code_service.infrastructure.dto.OpenAIRequest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OpenAIRequestTest {

    @Test
    void testConstructorAndGetters() {
        String model = "gpt-4";
        String prompt = "Hello world";
        OpenAIRequest request = new OpenAIRequest(model, prompt);

        assertEquals(model, request.getModel());

        List<Map<String, String>> messages = request.getMessages();
        assertNotNull(messages);
        assertEquals(1, messages.size());

        Map<String, String> message = messages.get(0);
        assertEquals("user", message.get("role"));
        assertEquals(prompt, message.get("content"));
    }

    @Test
    void testSetters() {
        OpenAIRequest request = new OpenAIRequest("gpt-3", "Prompt");

        String newModel = "gpt-4";
        List<Map<String, String>> newMessages = List.of(
                Map.of("role", "assistant", "content", "Hi there!")
        );

        request.setModel(newModel);
        request.setMessages(newMessages);

        assertEquals(newModel, request.getModel());
        assertEquals(newMessages, request.getMessages());
    }
}
