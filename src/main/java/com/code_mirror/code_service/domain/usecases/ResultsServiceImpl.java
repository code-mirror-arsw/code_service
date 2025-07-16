package com.code_mirror.code_service.domain.usecases;

import com.code_mirror.code_service.domain.ports.ResultsService;
import com.code_mirror.code_service.infrastructure.repository.EvaluationResultRepository;
import com.code_mirror.code_service.infrastructure.repository.entities.EvaluationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResultsServiceImpl implements ResultsService {

    @Autowired
    private EvaluationResultRepository repository;

    @Override
    public EvaluationResult create(EvaluationResult evaluationResult) {
        return repository.save(evaluationResult);
    }
    @Override
    public List<EvaluationResult> findAll() {
        return repository.findAll();
    }
    @Override
    public Optional<EvaluationResult> findById(Long id) {
        return repository.findById(id);
    }
    @Override
    public EvaluationResult update(Long id, EvaluationResult updated) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setScore(updated.getScore());
                    existing.setFeedback(updated.getFeedback());
                    existing.setSuggestions(updated.getSuggestions());
                    existing.setParticipants(updated.getParticipants());
                    return repository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("EvaluationResult not found with id: " + id));
    }
    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Page<EvaluationResult> getEvaluationsByEmail(String email, int page){
        Pageable pageable = PageRequest.of(page, 6);
        return repository.findByParticipantEmail(email,pageable);
    }

    @Override
    public Page<EvaluationResult> getEvaluationsByAdminEmail(String email, int page){
        Pageable pageable = PageRequest.of(page, 3);
        return repository.findByAdminEmail(email,pageable);
    }

    @Override
    public Page<EvaluationResult> getPassedEvaluationsByAdminEmail(String email, int page) {
        Pageable pageable = PageRequest.of(page, 3);
        return repository.findPassedByAdminEmail(email, pageable);
    }

    @Override
    public Page<EvaluationResult> getFailedEvaluationsByAdminEmail(String email, int page) {
        Pageable pageable = PageRequest.of(page, 3);
        return repository.findFailedByAdminEmail(email, pageable);
    }

    @Override
    public int countPassedEvaluationsByAdminEmail(String email) {
        return repository.countByAdminEmailAndScoreGreaterThanEqual(email, 70);
    }

    @Override
    public int countFailedEvaluationsByAdminEmail(String email) {
        return repository.countByAdminEmailAndScoreLessThan(email, 70);
    }





}
