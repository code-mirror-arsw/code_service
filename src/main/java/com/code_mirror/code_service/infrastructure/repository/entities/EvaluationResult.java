package com.code_mirror.code_service.infrastructure.repository.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int score;

    @Lob
    private String feedback;

    @ElementCollection
    private List<String> suggestions;

    @ElementCollection
    private List<String> participants;

    private String adminEmail;

    public EvaluationResult(int score, String feedback, List<String> suggestions, List<String> participants,
                            String adminEmail) {
        this.score = score;
        this.feedback = feedback;
        this.suggestions = suggestions;
        this.participants = participants;
        this.adminEmail = adminEmail;
    }
}
