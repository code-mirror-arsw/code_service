package com.code_mirror.code_service.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationResultDto {
    private int score;
    private String feedback;
    private List<String> suggestions;
}
