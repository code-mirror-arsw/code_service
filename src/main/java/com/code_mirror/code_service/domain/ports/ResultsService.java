package com.code_mirror.code_service.domain.ports;

import com.code_mirror.code_service.infrastructure.repository.entities.EvaluationResult;

import java.util.List;
import java.util.Optional;

public interface ResultsService {
    EvaluationResult create(EvaluationResult evaluationResult);

    List<EvaluationResult> findAll();

    Optional<EvaluationResult> findById(Long id);

    EvaluationResult update(Long id, EvaluationResult updated);

    void delete(Long id);
}
