package com.code_mirror.code_service.domain.usecases;

import com.code_mirror.code_service.domain.ports.AIClient;
import com.code_mirror.code_service.domain.ports.ResultsService;
import com.code_mirror.code_service.infrastructure.dto.EvaluationRequestDto;
import com.code_mirror.code_service.infrastructure.dto.EvaluationResultDto;
import com.code_mirror.code_service.infrastructure.dto.ProblemRequestDto;
import com.code_mirror.code_service.infrastructure.messaging.KafkaProducer;
import com.code_mirror.code_service.infrastructure.repository.CodingProblem;
import com.code_mirror.code_service.infrastructure.repository.entities.EvaluationResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

// NOTE: The DTOs and other dependent classes are assumed to exist.
// They are included at the bottom of this file for the code to be complete and runnable.

@ExtendWith(MockitoExtension.class)
class ProblemServiceImplTest {

    @Mock
    private AIClient aiClient;

    @Mock
    private RedisTemplate<String, CodingProblem> redisTemplate;

    @Mock
    private ValueOperations<String, CodingProblem> valueOperations;

    @Mock
    private ResultsService resultsService;

    @Mock
    private KafkaProducer kafkaProducer;

    @InjectMocks
    private ProblemServiceImpl problemService;

    private String roomId;
    private CodingProblem sampleProblem;

    @BeforeEach
    void setUp() {
        roomId = "test-room-123";
        sampleProblem = new CodingProblem();
        sampleProblem.setId(UUID.randomUUID().toString());
        sampleProblem.setTitle("Sample Problem");

        // Mock the RedisTemplate chain to avoid NullPointerExceptions
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }


    @Test
    void generateProblem_whenProblemExistsInRedis_returnsExistingProblem() {
        // Arrange: A problem for the given roomId already exists in Redis.
        ProblemRequestDto request = new ProblemRequestDto();
        request.setLanguage("Java");
        when(valueOperations.get(roomId)).thenReturn(sampleProblem);

        // Act: Call the method to generate a problem.
        CodingProblem result = problemService.generateProblem(request, roomId);

        // Assert: The existing problem is returned.
        assertNotNull(result);
        assertEquals(sampleProblem.getId(), result.getId());
        assertEquals(sampleProblem.getTitle(), result.getTitle());

        // Verify: The AI client and Redis 'set' are never called, as we had a cache hit.
        verify(aiClient, never()).generateCodingProblem(anyString());
        verify(valueOperations, never()).set(anyString(), any(CodingProblem.class));
    }

    @Test
    void generateProblem_whenProblemNotInRedis_generatesNewProblemAndSavesToRedis() {
        // Arrange: No problem exists in Redis for the given roomId.
        ProblemRequestDto request = new ProblemRequestDto();
        request.setLanguage("Python");

        CodingProblem newProblemFromAI = new CodingProblem();
        newProblemFromAI.setTitle("New Python Problem");

        when(valueOperations.get(roomId)).thenReturn(null);
        when(aiClient.generateCodingProblem(request.getLanguage())).thenReturn(Mono.just(newProblemFromAI));

        // Act: Call the method to generate a problem.
        CodingProblem result = problemService.generateProblem(request, roomId);

        // Assert: A new problem is returned with a generated ID.
        assertNotNull(result);
        assertNotNull(result.getId()); // A new UUID should be generated.
        assertEquals("New Python Problem", result.getTitle());

        // Verify: The AI client was called, and the new problem was saved to Redis.
        verify(aiClient, times(1)).generateCodingProblem("Python");
        verify(valueOperations, times(1)).set(roomId, newProblemFromAI);
    }

    // --- Tests for evaluateSolution ---

    @Test
    void evaluateSolution_whenProblemNotFoundInRedis_throwsIllegalArgumentException() {
        // Arrange: No problem is associated with the roomId in Redis.
        EvaluationRequestDto request = new EvaluationRequestDto();
        when(valueOperations.get(roomId)).thenReturn(null);

        // Act & Assert: An IllegalArgumentException is thrown.
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            problemService.evaluateSolution(roomId, request);
        });

        assertEquals("No hay problema registrado con ese roomId.", exception.getMessage());

        // Verify: No external services were called.
        verify(aiClient, never()).evaluateCode(any(), any());
        verify(resultsService, never()).create(any());
        verify(kafkaProducer, never()).sendMessage(any(), any());
    }



}

