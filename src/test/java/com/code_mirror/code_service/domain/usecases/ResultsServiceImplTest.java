package com.code_mirror.code_service.domain.usecases;

import com.code_mirror.code_service.infrastructure.repository.EvaluationResultRepository;
import com.code_mirror.code_service.infrastructure.repository.entities.EvaluationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResultsServiceImplTest {

    @Mock
    private EvaluationResultRepository repository;

    @InjectMocks
    private ResultsServiceImpl service;

    @Test
    void create_savesEvaluationResult() {
        EvaluationResult input = new EvaluationResult();
        when(repository.save(input)).thenReturn(input);

        EvaluationResult result = service.create(input);
        assertEquals(input, result);
        verify(repository).save(input);
    }

    @Test
    void findAll_returnsList() {
        List<EvaluationResult> list = List.of(new EvaluationResult());
        when(repository.findAll()).thenReturn(list);

        List<EvaluationResult> result = service.findAll();
        assertEquals(1, result.size());
        verify(repository).findAll();
    }

    @Test
    void findById_whenExists_returnsOptional() {
        EvaluationResult mock = new EvaluationResult();
        when(repository.findById(1L)).thenReturn(Optional.of(mock));

        Optional<EvaluationResult> result = service.findById(1L);
        assertTrue(result.isPresent());
        verify(repository).findById(1L);
    }

    @Test
    void findById_whenNotExists_returnsEmpty() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        Optional<EvaluationResult> result = service.findById(1L);
        assertTrue(result.isEmpty());
        verify(repository).findById(1L);
    }

    @Test
    void update_whenFound_updatesAndReturns() {
        EvaluationResult existing = new EvaluationResult();
        existing.setScore(50);

        EvaluationResult updated = new EvaluationResult();
        updated.setScore(90);
        updated.setFeedback("Good job");
        updated.setSuggestions(List.of("Improve X"));
        updated.setParticipants(List.of("a@a.com"));

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any())).thenReturn(existing);

        EvaluationResult result = service.update(1L, updated);
        assertEquals(90, result.getScore());
        verify(repository).save(existing);
    }

    @Test
    void update_whenNotFound_throwsException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        EvaluationResult updated = new EvaluationResult();
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                service.update(1L, updated)
        );

        assertTrue(ex.getMessage().contains("not found"));
    }

    @Test
    void delete_deletesById() {
        service.delete(5L);
        verify(repository).deleteById(5L);
    }

    @Test
    void getEvaluationsByEmail_returnsPage() {
        Page<EvaluationResult> page = new PageImpl<>(List.of(new EvaluationResult()));
        when(repository.findByParticipantEmail("x@x.com", PageRequest.of(0, 6))).thenReturn(page);

        Page<EvaluationResult> result = service.getEvaluationsByEmail("x@x.com", 0);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getEvaluationsByAdminEmail_returnsPage() {
        Page<EvaluationResult> page = new PageImpl<>(List.of(new EvaluationResult()));
        when(repository.findByAdminEmail("admin@x.com", PageRequest.of(0, 3))).thenReturn(page);

        Page<EvaluationResult> result = service.getEvaluationsByAdminEmail("admin@x.com", 0);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getPassedEvaluationsByAdminEmail_returnsPage() {
        Page<EvaluationResult> page = new PageImpl<>(List.of(new EvaluationResult()));
        when(repository.findPassedByAdminEmail("admin@x.com", PageRequest.of(0, 3))).thenReturn(page);

        Page<EvaluationResult> result = service.getPassedEvaluationsByAdminEmail("admin@x.com", 0);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getFailedEvaluationsByAdminEmail_returnsPage() {
        Page<EvaluationResult> page = new PageImpl<>(List.of(new EvaluationResult()));
        when(repository.findFailedByAdminEmail("admin@x.com", PageRequest.of(0, 3))).thenReturn(page);

        Page<EvaluationResult> result = service.getFailedEvaluationsByAdminEmail("admin@x.com", 0);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void countPassedEvaluationsByAdminEmail_returnsCount() {
        when(repository.countByAdminEmailAndScoreGreaterThanEqual("admin@x.com", 70)).thenReturn(5);

        int count = service.countPassedEvaluationsByAdminEmail("admin@x.com");
        assertEquals(5, count);
    }

    @Test
    void countFailedEvaluationsByAdminEmail_returnsCount() {
        when(repository.countByAdminEmailAndScoreLessThan("admin@x.com", 70)).thenReturn(2);

        int count = service.countFailedEvaluationsByAdminEmail("admin@x.com");
        assertEquals(2, count);
    }
}
