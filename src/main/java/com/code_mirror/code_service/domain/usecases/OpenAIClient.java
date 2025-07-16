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
    Actúa como un evaluador automático de soluciones de programación para entrevistas técnicas. 
    Analiza estrictamente si el siguiente código resuelve completamente el problema planteado, sin errores, y si implementa todos los requisitos obligatorios.

    Problema:
    - Título: %s
    - Descripción: %s
    - Lenguaje requerido: %s

    Código del usuario:
    %s

    Evalúa lo siguiente:
    1. Verifica que el código esté escrito en el lenguaje solicitado (**%s**). 
       Si no lo está, retorna esta respuesta JSON exacta:
       {
         "success": false,
         "score": 0,
         "feedback": "El código no está escrito en el lenguaje solicitado: %s"
       }

    2. Si el lenguaje es correcto:
       - ¿La solución implementa correctamente la lógica requerida?
       - ¿Cumple con TODAS las restricciones y validaciones del enunciado?
       - ¿Contiene todos los métodos necesarios y está estructurada como una solución ejecutable y completa?
       - Si el código está incompleto o le falta la función principal, debe recibir una puntuación BAJA (<50).
       - Si el código tiene errores graves, asigna un score bajo y marca `success: false`.

    Devuelve SOLO un JSON con este formato:

    {
      "success": true,
      "score": 0,
      "feedback": "Explicación crítica de lo que falta o está mal"
    }
    """.formatted(
                problem.getTitle(),
                problem.getDescription(),
                problem.getLanguage(),
                userCode,
                problem.getLanguage(),
                problem.getLanguage()
        );

        return callOpenAI(prompt, EvaluationResultDto.class);
    }



    private <T> Mono<T> callOpenAI(String prompt, Class<T> responseType) {
        return openAIWebClient.post()
                .uri("/v1/chat/completions")
                .header("Authorization", "Bearer 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")
                .header("Content-Type", "application/json")
                .bodyValue(new OpenAIRequest("deepseek/deepseek-chat-v3-0324:free", prompt))
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(responseText -> {
                    try {
                        String content = objectMapper.readTree(responseText)
                                .path("choices").get(0)
                                .path("message")
                                .path("content")
                                .asText();

                        String cleanedJson = content
                                .replaceAll("(?i)^```json\\s*", "")
                                .replaceAll("```$", "")
                                .trim();

                        return Mono.just(objectMapper.readValue(cleanedJson, responseType));
                    } catch (Exception e) {
                        return Mono.error(new RuntimeException("Error parsing OpenAI response", e));
                    }
                });
    }


}
