package com.code_mirror.code_service.infrastruture;

import com.code_mirror.code_service.infrastructure.repository.entities.EvaluationResult;
import org.junit.jupiter.api.Test;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EvaluationResultTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        EvaluationResult result = new EvaluationResult();
        result.setId(1L);
        result.setScore(90);
        result.setFeedback("Excelente solución");
        result.setSuggestions(List.of("Usar más funciones", "Optimizar loops"));
        result.setParticipants(List.of("maria@example.com", "pedro@example.com"));
        result.setAdminEmail("admin@code.com");

        assertEquals(1L, result.getId());
        assertEquals(90, result.getScore());
        assertEquals("Excelente solución", result.getFeedback());
        assertEquals(2, result.getSuggestions().size());
        assertTrue(result.getParticipants().contains("maria@example.com"));
        assertEquals("admin@code.com", result.getAdminEmail());
    }

    @Test
    void testAllArgsConstructor() {
        EvaluationResult result = new EvaluationResult(
                88,
                "Buen enfoque",
                List.of("Usar nombres descriptivos"),
                List.of("dev1@example.com", "dev2@example.com"),
                "admin@example.com"
        );

        assertNull(result.getId()); // Porque este constructor no lo asigna
        assertEquals(88, result.getScore());
        assertEquals("Buen enfoque", result.getFeedback());
        assertEquals("admin@example.com", result.getAdminEmail());
    }
}
