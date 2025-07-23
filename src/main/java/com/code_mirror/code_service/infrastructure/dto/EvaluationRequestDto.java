package com.code_mirror.code_service.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EvaluationRequestDto {
    private String solutionCode;
    private List<String> participants;
    private String adminEmail;

    public EvaluationRequestDto(String solutionCode) {
        this.solutionCode = solutionCode;
    }
}