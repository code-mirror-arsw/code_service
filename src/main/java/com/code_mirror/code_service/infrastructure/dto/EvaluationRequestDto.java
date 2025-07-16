package com.code_mirror.code_service.infrastructure.dto;

import lombok.Data;

import java.util.List;

@Data
public class EvaluationRequestDto {
    private String solutionCode;
    private List<String> participants;
    private String adminEmail;

}