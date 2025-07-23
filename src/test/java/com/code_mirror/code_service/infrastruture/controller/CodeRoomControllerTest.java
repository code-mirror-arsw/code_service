package com.code_mirror.code_service.infrastruture.controller;

import com.code_mirror.code_service.domain.ports.ProblemService;
import com.code_mirror.code_service.infrastructure.controller.CodeRoomController;
import com.code_mirror.code_service.infrastructure.dto.EvaluationRequestDto;
import com.code_mirror.code_service.infrastructure.dto.EvaluationResultDto;
import com.code_mirror.code_service.infrastructure.dto.ProblemRequestDto;
import com.code_mirror.code_service.infrastructure.repository.CodingProblem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class CodeRoomControllerTest {

    @Mock
    private ProblemService service;

    @InjectMocks
    private CodeRoomController controller;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGenerateProblem_Success() {
        ProblemRequestDto request = new ProblemRequestDto("java");
        String roomId = "room123";
        CodingProblem mockProblem = new CodingProblem();
        mockProblem.setId("problem123");

        when(service.generateProblem(request, roomId)).thenReturn(mockProblem);

        ResponseEntity<?> response = controller.generateProblem(request, roomId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockProblem, response.getBody());
        verify(service, times(1)).generateProblem(request, roomId);
    }

    @Test
    public void testGenerateProblem_Failure() {
        ProblemRequestDto request = new ProblemRequestDto("java");
        String roomId = "room123";

        when(service.generateProblem(request, roomId))
                .thenThrow(new RuntimeException("Error interno"));

        ResponseEntity<?> response = controller.generateProblem(request, roomId);

        assertEquals(500, response.getStatusCodeValue());
        verify(service, times(1)).generateProblem(request, roomId);
    }

    @Test
    public void testGradeProblem_Success() {
        String roomId = "room123";
        EvaluationRequestDto code = new EvaluationRequestDto("System.out.println(\"Hello\");");

        EvaluationResultDto result = new EvaluationResultDto(90, "Buen trabajo", List.of("Podrías usar un bucle"));

        when(service.evaluateSolution(roomId, code)).thenReturn(result);

        ResponseEntity<?> response = controller.gradeProblem(roomId, code);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(result, response.getBody());
        verify(service, times(1)).evaluateSolution(roomId, code);
    }

    @Test
    public void testGradeProblem_Failure() {
        String roomId = "room123";
        EvaluationRequestDto code = new EvaluationRequestDto("bad code");

        when(service.evaluateSolution(roomId, code))
                .thenThrow(new RuntimeException("Evaluación fallida"));

        ResponseEntity<?> response = controller.gradeProblem(roomId, code);

        assertEquals(500, response.getStatusCodeValue());
        verify(service, times(1)).evaluateSolution(roomId, code);
    }
}
