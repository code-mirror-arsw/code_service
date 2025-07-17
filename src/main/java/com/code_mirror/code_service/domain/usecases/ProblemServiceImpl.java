package com.code_mirror.code_service.domain.usecases;

import com.code_mirror.code_service.domain.ports.AIClient;
import com.code_mirror.code_service.domain.ports.ProblemService;
import com.code_mirror.code_service.domain.ports.ResultsService;
import com.code_mirror.code_service.infrastructure.dto.EvaluationRequestDto;
import com.code_mirror.code_service.infrastructure.dto.EvaluationResultDto;
import com.code_mirror.code_service.infrastructure.dto.ProblemRequestDto;
import com.code_mirror.code_service.infrastructure.messaging.FcmMessage;
import com.code_mirror.code_service.infrastructure.messaging.KafkaProducer;
import com.code_mirror.code_service.infrastructure.messaging.NotificationType;
import com.code_mirror.code_service.infrastructure.repository.CodingProblem;
import com.code_mirror.code_service.infrastructure.repository.entities.EvaluationResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@Service
public class ProblemServiceImpl implements ProblemService {

    @Autowired
    private AIClient aiClient;
    @Autowired
    private RedisTemplate<String, CodingProblem> redisTemplate;
    @Autowired
    private ResultsService resultsService;
    @Autowired
    KafkaProducer kafkaProducer;

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

        notificationsClients(evaluationResult);
        resultsService.create(evaluationResult);

        return result;
    }

    private void notificationsClients(EvaluationResult evaluationResult) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        for (String email : evaluationResult.getParticipants()) {
            FcmMessage message = FcmMessage.builder()
                    .to(email)
                    .source(NotificationType.APPLICATION_RESULT_AVAILABLE)
                    .data(
                            Map.of(
                                    "feedback", evaluationResult.getFeedback(),
                                    "score", String.valueOf(evaluationResult.getScore())
                            )
                    )
                    .build();

            try {
                String json = objectMapper.writeValueAsString(message);
                kafkaProducer.sendMessage("notification-topic", json);
                System.out.println("✅ Mensaje enviado a: " + email);
            } catch (JsonProcessingException e) {
                System.err.println("❌ Error convirtiendo FcmMessage a JSON para email " + email + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }



}
