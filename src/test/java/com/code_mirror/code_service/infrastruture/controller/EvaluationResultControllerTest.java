package com.code_mirror.code_service.infrastruture.controller;

import com.code_mirror.code_service.domain.ports.ResultsService;
import com.code_mirror.code_service.infrastructure.controller.EvaluationResultController;
import com.code_mirror.code_service.infrastructure.repository.entities.EvaluationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EvaluationResultControllerTest {

    @InjectMocks
    private EvaluationResultController controller;

    @Mock
    private ResultsService resultsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreate() {
        EvaluationResult input = new EvaluationResult();
        EvaluationResult saved = new EvaluationResult();
        when(resultsService.create(input)).thenReturn(saved);

        var response = controller.create(input);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(saved, response.getBody());
    }

    @Test
    void testGetAll() {
        List<EvaluationResult> list = List.of(new EvaluationResult(), new EvaluationResult());
        when(resultsService.findAll()).thenReturn(list);

        var response = controller.getAll();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(list, response.getBody());
    }

    @Test
    void testUpdateSuccess() {
        EvaluationResult updated = new EvaluationResult();
        when(resultsService.update(1L, updated)).thenReturn(updated);

        var response = controller.update(1L, updated);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(updated, response.getBody());
    }

    @Test
    void testUpdateNotFound() {
        EvaluationResult updated = new EvaluationResult();
        when(resultsService.update(1L, updated)).thenThrow(new RuntimeException("Not found"));

        var response = controller.update(1L, updated);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Not found", response.getBody());
    }

    @Test
    void testDeleteSuccess() {
        doNothing().when(resultsService).delete(1L);

        var response = controller.delete(1L);

        assertEquals(204, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void testDeleteFail() {
        doThrow(new RuntimeException()).when(resultsService).delete(1L);

        var response = controller.delete(1L);

        assertEquals(404, response.getStatusCodeValue());
        assertTrue(((String) response.getBody()).contains("No se pudo eliminar"));
    }

    @Test
    void testGetByParticipant() {
        Page<EvaluationResult> page = new PageImpl<>(List.of(new EvaluationResult()));
        when(resultsService.getEvaluationsByEmail("test@example.com", 0)).thenReturn(page);

        var response = controller.getByParticipant("test@example.com", 0);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(page, response.getBody());
    }

    @Test
    void testGetByAdminEmail() {
        Page<EvaluationResult> page = new PageImpl<>(List.of(new EvaluationResult()));
        when(resultsService.getEvaluationsByAdminEmail("admin@example.com", 0)).thenReturn(page);

        var response = controller.getByAdminEmail("admin@example.com", 0);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(page, response.getBody());
    }

    @Test
    void testGetPassedByAdmin() {
        Page<EvaluationResult> page = new PageImpl<>(List.of(new EvaluationResult()));
        when(resultsService.getPassedEvaluationsByAdminEmail("admin@example.com", 0)).thenReturn(page);

        var response = controller.getPassedByAdmin("admin@example.com", 0);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(page, response.getBody());
    }

    @Test
    void testGetFailedByAdmin() {
        Page<EvaluationResult> page = new PageImpl<>(List.of(new EvaluationResult()));
        when(resultsService.getFailedEvaluationsByAdminEmail("admin@example.com", 0)).thenReturn(page);

        var response = controller.getFailedByAdmin("admin@example.com", 0);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(page, response.getBody());
    }

    @Test
    void testCountPassedByAdmin() {
        when(resultsService.countPassedEvaluationsByAdminEmail("admin@example.com")).thenReturn(5);

        var response = controller.countPassedByAdmin("admin@example.com");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(5, response.getBody().get("count"));
    }

    @Test
    void testCountFailedByAdmin() {
        when(resultsService.countFailedEvaluationsByAdminEmail("admin@example.com")).thenReturn(2);

        var response = controller.countFailedByAdmin("admin@example.com");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().get("count"));
    }
}
