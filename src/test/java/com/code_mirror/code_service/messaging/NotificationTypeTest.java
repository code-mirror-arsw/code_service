package com.code_mirror.code_service.messaging;


import com.code_mirror.code_service.infrastructure.messaging.NotificationType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTypeTest {

    @Test
    void testEnumValues() {
        NotificationType[] values = NotificationType.values();

        assertTrue(values.length > 0);
        assertNotNull(NotificationType.valueOf("INTERVIEW_REMINDER_ONE_HOUR"));
        assertEquals("APPLICATION_RESULT_AVAILABLE", NotificationType.APPLICATION_RESULT_AVAILABLE.name());
    }
}
