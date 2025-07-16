package com.code_mirror.code_service.infrastructure.controller;


import com.code_mirror.code_service.domain.ports.ResultsService;
import com.code_mirror.code_service.infrastructure.repository.entities.EvaluationResult;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador REST para gestionar resultados de evaluación.
 */
@RestController
@RequestMapping("/evaluation-results")
public class EvaluationResultController {

    @Autowired
    private ResultsService resultsService;

    /**
     * Crea un nuevo resultado de evaluación.
     *
     * @param result Objeto EvaluationResult a guardar.
     * @return EvaluationResult creado con código 201.
     */
    @PostMapping
    public ResponseEntity<EvaluationResult> create(@Valid @RequestBody EvaluationResult result) {
        EvaluationResult saved = resultsService.create(result);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    /**
     * Obtiene todos los resultados de evaluación.
     *
     * @return Lista de EvaluationResult con código 200.
     */
    @GetMapping
    public ResponseEntity<List<EvaluationResult>> getAll() {
        return ResponseEntity.ok(resultsService.findAll());
    }

    /**
     * Actualiza un resultado existente.
     *
     * @param id      ID del resultado a actualizar.
     * @param updated Objeto EvaluationResult con nuevos datos.
     * @return EvaluationResult actualizado o error si no se encuentra.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody EvaluationResult updated) {
        try {
            EvaluationResult result = resultsService.update(id, updated);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Elimina un resultado por ID.
     *
     * @param id ID del resultado a eliminar.
     * @return Código 204 si se elimina o mensaje de error.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            resultsService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se pudo eliminar la evaluación con ID: " + id);
        }
    }

    /**
     * Obtiene todos los resultados en los que participó un usuario dado su correo.
     *
     * @param email Correo del participante.
     * @return Lista de resultados donde el correo aparece en participantes.
     */
    @GetMapping("/by-participant")
    public ResponseEntity<Page<EvaluationResult>> getByParticipant(@RequestParam String email
            , @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(resultsService.getEvaluationsByEmail(email,page));
    }

    @GetMapping("/by-admin")
    public ResponseEntity<Page<EvaluationResult>> getByAdminEmail(@RequestParam String email
            , @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(resultsService.getEvaluationsByAdminEmail(email,page));
    }

    @GetMapping("/by-admin/passed")
    public ResponseEntity<Page<EvaluationResult>> getPassedByAdmin(
            @RequestParam String email,
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(resultsService.getPassedEvaluationsByAdminEmail(email, page));
    }

    @GetMapping("/by-admin/failed")
    public ResponseEntity<Page<EvaluationResult>> getFailedByAdmin(
            @RequestParam String email,
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(resultsService.getFailedEvaluationsByAdminEmail(email, page));
    }

    @GetMapping("/by-admin/passed/count")
    public ResponseEntity<Map<String, Integer>> countPassedByAdmin(@RequestParam String email) {
        int count = resultsService.countPassedEvaluationsByAdminEmail(email);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @GetMapping("/by-admin/failed/count")
    public ResponseEntity<Map<String, Integer>> countFailedByAdmin(@RequestParam String email) {
        int count = resultsService.countFailedEvaluationsByAdminEmail(email);
        return ResponseEntity.ok(Map.of("count", count));
    }

}
