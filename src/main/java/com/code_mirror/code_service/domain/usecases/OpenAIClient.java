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

/**
 * Client for interacting with the OpenAI API to generate coding problems and evaluate code.
 * Implements the {@link AIClient} interface.
 */
@Service
public class OpenAIClient implements AIClient {

    @Autowired
    private WebClient openAIWebClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${openai.api.model}")
    private String openApiModel;

    @Value("${openai.api.key}")
    private String openApiKey;


    /**
     * Generates an original coding problem of medium difficulty using the specified language.
     * The problem includes a title, description, language, and example test cases.
     *
     * @param language The programming language for which the problem should be generated.
     * @return A {@link Mono} emitting a {@link CodingProblem} object.
     */


    @Override
    public Mono<CodingProblem> generateCodingProblem(String language) {
        String prompt = """
        Genera un problema de programación original de dificultad media usando el lenguaje '%s'.
        Evita problemas simples como contar o sumar elementos. Usa estructuras de datos como listas, diccionarios, árboles o matrices cuando sea posible.
        Tu respuesta DEBE ser exclusivamente un JSON con esta estructura exacta:
        
        {
          "title": "Título del Problema",
          "description": "Descripción detallada del problema, incluyendo restricciones.",
          "language": "%s",
          "exampleTestCases": [
            {"input": "ejemplo de entrada 1", "expectedOutput": "ejemplo de salida 1"},
            {"input": "ejemplo de entrada 2", "expectedOutput": "ejemplo de salida 2"}
          ]
        }
        No incluyas ningún texto antes o después del JSON. Solo el objeto JSON.
        """.formatted(language, language);


        return callOpenAI(prompt, CodingProblem.class);
    }

    /**
     * Evaluates user-submitted code against a given coding problem.
     * It checks for language correctness, logical implementation, adherence to constraints, and completeness.
     *
     * @param problem The {@link CodingProblem} to evaluate against.
     * @param userCode The user's submitted code as a String.
     * @return A {@link Mono} emitting an {@link EvaluationResultDto} containing success status, score, and feedback.
     */
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



    /**
     * Calls the OpenAI API with a given prompt and parses the response into the specified type.
     * Handles the API request, authorization, and response parsing, including cleaning up markdown fences from the JSON response.
     *
     * @param prompt The prompt string to send to OpenAI.
     * @param responseType The class type to which the JSON response content should be mapped.
     * @param <T> The type of the expected response object.
     * @return A {@link Mono} emitting an object of type T, or an error if parsing fails.
     */
    private <T> Mono<T> callOpenAI(String prompt, Class<T> responseType) {
        return openAIWebClient.post()
                .uri("/v1/chat/completions")
                .header("Authorization", "Bearer " + openApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(new OpenAIRequest(openApiModel, prompt))
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
