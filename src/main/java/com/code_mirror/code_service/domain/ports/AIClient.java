package com.code_mirror.code_service.domain.ports;

import com.code_mirror.code_service.infrastructure.dto.EvaluationResultDto;
import com.code_mirror.code_service.infrastructure.repository.CodingProblem;
import reactor.core.publisher.Mono;

public interface AIClient {
    Mono<CodingProblem> generateCodingProblem(String language);
    Mono<EvaluationResultDto> evaluateCode(CodingProblem problem, String userCode);
}