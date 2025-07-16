package com.code_mirror.code_service.domain.usecases;

import com.code_mirror.code_service.domain.ports.AIClient;
import com.code_mirror.code_service.domain.ports.ProblemService;
import com.code_mirror.code_service.domain.ports.ResultsService;
import com.code_mirror.code_service.infrastructure.dto.EvaluationRequestDto;
import com.code_mirror.code_service.infrastructure.dto.EvaluationResultDto;
import com.code_mirror.code_service.infrastructure.dto.ProblemRequestDto;
import com.code_mirror.code_service.infrastructure.repository.CodingProblem;
import com.code_mirror.code_service.infrastructure.repository.entities.EvaluationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class ProblemServiceImpl implements ProblemService {

    @Autowired
    private AIClient aiClient;
    @Autowired
    private RedisTemplate<String, CodingProblem> redisTemplate;
    @Autowired
    private ResultsService resultsService;

    @Override
    public CodingProblem generateProblem(ProblemRequestDto request, String roomId) {
        CodingProblem existing = redisTemplate.opsForValue().get(roomId);
        if (existing != null) {
            return existing;
        }

        Mono<CodingProblem> mono = aiClient.generateCodingProblem(request.getLanguage());

        return mono.map(problem -> {
            problem.setId(String.valueOf(UUID.randomUUID()));
            redisTemplate.opsForValue().set(roomId, problem);
            return problem;
        }).block();
    }


    @Override
    public EvaluationResultDto evaluateSolution(String roomId, EvaluationRequestDto request) {
        CodingProblem problem = redisTemplate.opsForValue().get(roomId);
        if (problem == null) {
            throw new IllegalArgumentException("No hay problema registrado con ese roomId.");
        }

        Mono<EvaluationResultDto> mono = aiClient.evaluateCode(problem, request.getSolutionCode());
        EvaluationResultDto result = mono.block();

        EvaluationResult evaluationResult = new EvaluationResult(result.getScore(), result.getFeedback(),
                result.getSuggestions(), request.getParticipants(),request.getAdminEmail());

        resultsService.create(evaluationResult);

        return result;
    }


}
