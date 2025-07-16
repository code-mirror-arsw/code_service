package com.code_mirror.code_service.infrastructure.repository;

import com.code_mirror.code_service.infrastructure.repository.entities.EvaluationResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationResultRepository extends JpaRepository<EvaluationResult, Long> {
    @Query("SELECT e FROM EvaluationResult e JOIN e.participants p WHERE p = :email")
    Page<EvaluationResult> findByParticipantEmail(String email, Pageable pageable);

    Page<EvaluationResult> findByAdminEmail(String adminEmail, Pageable pageable);

    @Query("SELECT e FROM EvaluationResult e WHERE e.adminEmail = :adminEmail AND e.score >= 70")
    Page<EvaluationResult> findPassedByAdminEmail(@Param("adminEmail") String adminEmail, Pageable pageable);

    @Query("SELECT e FROM EvaluationResult e WHERE e.adminEmail = :adminEmail AND e.score < 70")
    Page<EvaluationResult> findFailedByAdminEmail(@Param("adminEmail") String adminEmail, Pageable pageable);

    int countByAdminEmailAndScoreGreaterThanEqual(String email, int score);

    int countByAdminEmailAndScoreLessThan(String email, int score);




}
