package com.code_mirror.code_service.messaging;

import com.code_mirror.code_service.infrastructure.messaging.FcmMessage;
import com.code_mirror.code_service.infrastructure.messaging.NotificationType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FcmMessageTest {

    @Test
    void testBuilderAndGetters() {
        Map<String, String> data = new HashMap<>();
        data.put("key", "value");

        FcmMessage message = FcmMessage.builder()
                .to("user123")
                .source(NotificationType.INTERVIEW_ACCEPTED_LINK)
                .data(data)
                .build();

        assertEquals("user123", message.getTo());
        assertEquals(NotificationType.INTERVIEW_ACCEPTED_LINK, message.getSource());
        assertEquals("value", message.getData().get("key"));
    }

    @Test
    void testNoArgsConstructor() {
        FcmMessage message = new FcmMessage();
        assertNull(message.getTo());
        assertNull(message.getSource());
        assertNull(message.getData());
    }

    @Test
    void testAllArgsConstructor() {
        Map<String, String> data = new HashMap<>();
        data.put("code", "123");

        FcmMessage message = new FcmMessage("user456", NotificationType.INTERVIEW_REMINDER_ONE_HOUR, data);

        assertEquals("user456", message.getTo());
        assertEquals(NotificationType.INTERVIEW_REMINDER_ONE_HOUR, message.getSource());
        assertEquals("123", message.getData().get("code"));
    }

    @Test
    void testSetters() {
        FcmMessage message = new FcmMessage();
        message.setTo("to-user");
        message.setSource(NotificationType.APPLICATION_RESULT_AVAILABLE);
        Map<String, String> map = new HashMap<>();
        map.put("id", "001");
        message.setData(map);

        assertEquals("to-user", message.getTo());
        assertEquals(NotificationType.APPLICATION_RESULT_AVAILABLE, message.getSource());
        assertEquals("001", message.getData().get("id"));
    }
}
