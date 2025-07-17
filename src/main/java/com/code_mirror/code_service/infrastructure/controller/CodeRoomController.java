package com.code_mirror.code_service.infrastructure.controller;


import com.code_mirror.code_service.domain.ports.ProblemService;
import com.code_mirror.code_service.infrastructure.dto.EvaluationRequestDto;
import com.code_mirror.code_service.infrastructure.dto.EvaluationResultDto;
import com.code_mirror.code_service.infrastructure.dto.ProblemRequestDto;
import com.code_mirror.code_service.infrastructure.repository.CodingProblem;
import com.code_mirror.code_service.infrastructure.repository.entities.EvaluationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST para gestionar las salas de código (Code Rooms).
 * Expone endpoints para crear, obtener, actualizar y eliminar salas.
 */
@RestController
@RequestMapping("/code")
public class CodeRoomController {

    @Autowired
    private ProblemService service;

    /**
     * Crea una nueva sala de código.
     * @param request DTO con la información para crear la sala (ej. lenguaje).
     * @return La sala de código recién creada.
     */
    @PostMapping
    public ResponseEntity<?> generateProblem(@RequestBody ProblemRequestDto request,@RequestParam String roomId) {
        try {
            CodingProblem code = service.generateProblem(request,roomId);
            return ResponseEntity.ok(code);
        } catch (Exception e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/grade")
    public ResponseEntity<?> gradeProblem(@RequestParam String roomId, @RequestBody EvaluationRequestDto code) {
        try {
            EvaluationResultDto gradeResult = service.evaluateSolution(roomId, code);
            return ResponseEntity.ok(gradeResult);
        } catch (Exception e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Método de utilidad para construir respuestas de error estandarizadas.
     * @param message El mensaje de error.
     * @param status El código de estado HTTP.
     * @return Un objeto ResponseEntity con el error.
     */
    private ResponseEntity<Map<String, String>> buildErrorResponse(String message, HttpStatus status) {
        Map<String, String> errorBody = Map.of(
                "status", String.valueOf(status.value()),
                "error", status.getReasonPhrase(),
                "message", message
        );
        return ResponseEntity.status(status).body(errorBody);
    }
}