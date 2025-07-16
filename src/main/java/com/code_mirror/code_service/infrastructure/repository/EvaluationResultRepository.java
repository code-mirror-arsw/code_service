package com.code_mirror.code_service.infrastructure.repository;

import com.code_mirror.code_service.infrastructure.repository.entities.EvaluationResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluationResultRepository extends JpaRepository<EvaluationResult, Long> {
}
