package com.code_mirror.code_service.domain.ports;


import com.code_mirror.code_service.infrastructure.dto.EvaluationRequestDto;
import com.code_mirror.code_service.infrastructure.dto.EvaluationResultDto;
import com.code_mirror.code_service.infrastructure.dto.ProblemRequestDto;
import com.code_mirror.code_service.infrastructure.repository.CodingProblem;

public interface ProblemService {
    CodingProblem generateProblem(ProblemRequestDto request,String roomId);
    EvaluationResultDto evaluateSolution(String problemId, EvaluationRequestDto request);
}