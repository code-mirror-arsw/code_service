package com.code_mirror.code_service.domain.ports;

import com.code_mirror.code_service.infrastructure.repository.entities.EvaluationResult;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface ResultsService {
    EvaluationResult create(EvaluationResult evaluationResult);

    List<EvaluationResult> findAll();

    Optional<EvaluationResult> findById(Long id);

    EvaluationResult update(Long id, EvaluationResult updated);

    void delete(Long id);

    Page<EvaluationResult> getEvaluationsByEmail(String email, int page);

    Page<EvaluationResult> getEvaluationsByAdminEmail(String email, int page);

    Page<EvaluationResult> getPassedEvaluationsByAdminEmail(String email, int page);

    Page<EvaluationResult> getFailedEvaluationsByAdminEmail(String email, int page);

    int countPassedEvaluationsByAdminEmail(String email);

    int countFailedEvaluationsByAdminEmail(String email);
}
