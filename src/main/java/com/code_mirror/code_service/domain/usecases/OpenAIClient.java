package com.code_mirror.code_service.domain.usecases;

import com.code_mirror.code_service.domain.ports.AIClient;
import com.code_mirror.code_service.infrastructure.dto.EvaluationResultDto;
import com.code_mirror.code_service.infrastructure.dto.OpenAIRequest;
import com.code_mirror.code_service.infrastructure.repository.CodingProblem;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class OpenAIClient implements AIClient {

    @Autowired
    private WebClient openAIWebClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${openai.api.model}")
    private String openApiModel;

    @Override
    public Mono<CodingProblem> generateCodingProblem(String language) {
        String prompt = """
            Genera un problema de programación de dificultad media para el lenguaje '%s', similar a los de HackerRank.
            Tu respuesta DEBE ser únicamente un objeto JSON con la siguiente estructura:
            {
              "title": "Título del Problema",
              "description": "Descripción detallada del problema, incluyendo restricciones.",
              "language": "%s",
              "exampleTestCases": [
                {"input": "ejemplo de entrada 1", "expectedOutput": "ejemplo de salida 1"},
                {"input": "ejemplo de entrada 2", "expectedOutput": "ejemplo de salida 2"}
              ]
            }
            No incluyas texto antes o después del JSON.
            """.formatted(language, language);

        return callOpenAI(prompt, CodingProblem.class);
    }

    @Override
    public Mono<EvaluationResultDto> evaluateCode(CodingProblem problem, String userCode) {
        String prompt = """
            Actúa como un juez experto en programación. Evalúa el siguiente código enviado por un usuario para resolver un problema.

            Problema:
            - Título: %s
            - Descripción: %s
            - Lenguaje: %s

            Código del usuario:
            %s

            Usa los casos de prueba de ejemplo y responde SOLO con un JSON con esta estructura:

            {
              "success": true,
              "score": 90,
              "feedback": "Explicación sobre qué funciona bien y qué se puede mejorar"
            }
            No incluyas texto adicional fuera del JSON.
            """.formatted(problem.getTitle(), problem.getDescription(), problem.getLanguage(), userCode);

        return callOpenAI(prompt, EvaluationResultDto.class);
    }

    private <T> Mono<T> callOpenAI(String prompt, Class<T> responseType) {
        return openAIWebClient.post()
                .uri("/v1/chat/completions")
                .bodyValue(new OpenAIRequest(openApiModel, prompt))
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(responseText -> {
                    try {
                        return Mono.just(objectMapper.readValue(responseText, responseType));
                    } catch (Exception e) {
                        return Mono.error(new RuntimeException("Error parsing OpenAI response", e));
                    }
                });
    }
}
