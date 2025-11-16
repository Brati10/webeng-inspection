package de.dhbw.webenginspection.controller;

import de.dhbw.webenginspection.entity.InspectionStep;
import de.dhbw.webenginspection.entity.StepStatus;
import de.dhbw.webenginspection.service.InspectionStepService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class InspectionStepController {
    
    private final InspectionStepService inspectionStepService;

    public InspectionStepController(InspectionStepService inspectionStepService) {
        this.inspectionStepService = inspectionStepService;
    }

    /**
     * Alle Steps einer Inspection abrufen.
     * GET /api/inspections/{inspectionId}/steps
     */
    @GetMapping("/inspections/{inspectionId}/steps")
    public List<InspectionStep> getStepsForInspection(@PathVariable Long inspectionId) {
        return inspectionStepService.getStepsForInspection(inspectionId);
    }

    /**
     * Steps einer Inspection nach Status filtern.
     * GET /api/inspections/{inspectionId}/steps/status/{status}
     * Beispiel: /api/inspections/1/steps/status/PASSED
     */
    @GetMapping("/inspections/{inspectionId}/steps/status/{status}")
    public ResponseEntity<List<InspectionStep>> getStepsForInspectionByStatus(
            @PathVariable Long inspectionId,
            @PathVariable String status) {

        try {
            StepStatus stepStatus = StepStatus.valueOf(status);
            List<InspectionStep> steps = inspectionStepService.getStepsForInspectionByStatus(inspectionId, stepStatus);
            return ResponseEntity.ok(steps);
        } catch (IllegalArgumentException e) {
            // z. B. wenn status kein gültiger Enum-Wert ist
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Einzelnen InspectionStep per ID abrufen.
     * GET /api/inspection-steps/{id}
     */
    @GetMapping("/inspection-steps/{id}")
    public ResponseEntity<InspectionStep> getStepById(@PathVariable Long id) {
        try {
            InspectionStep step = inspectionStepService.getStepById(id);
            return ResponseEntity.ok(step);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Neuen Step für eine bestehende Inspection anlegen.
     * POST /api/inspections/{inspectionId}/steps?checklistStepId=123
     *
     * Body enthält Status/Kommentar/PhotoPath.
     */
    @PostMapping("/inspections/{inspectionId}/steps")
    public ResponseEntity<InspectionStep> createStep(
            @PathVariable Long inspectionId,
            @RequestParam Long checklistStepId,
            @RequestBody InspectionStep stepData) {

        try {
            InspectionStep created = inspectionStepService.createStep(inspectionId, checklistStepId, stepData);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            // Inspection oder ChecklistStep nicht gefunden / fachlicher Fehler
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Vollständiges Update eines Steps.
     * PUT /api/inspection-steps/{id}
     */
    @PutMapping("/inspection-steps/{id}")
    public ResponseEntity<InspectionStep> updateStep(
            @PathVariable Long id,
            @RequestBody InspectionStep updated) {

        try {
            InspectionStep saved = inspectionStepService.updateStep(id, updated);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Nur den Status eines Steps ändern.
     * PATCH /api/inspection-steps/{id}/status
     *
     * Body: z. B. "PASSED" oder "FAILED"
     */
    @PatchMapping("/inspection-steps/{id}/status")
    public ResponseEntity<InspectionStep> updateStatus(
            @PathVariable Long id,
            @RequestBody String newStatus) {

        try {
            StepStatus status = StepStatus.valueOf(newStatus.trim());
            InspectionStep updated = inspectionStepService.updateStatus(id, status);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            // entweder Step nicht gefunden oder ungültiger Status
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Nur den Kommentar eines Steps ändern.
     * PATCH /api/inspection-steps/{id}/comment
     *
     * Body: reiner String mit dem Kommentar.
     */
    @PatchMapping("/inspection-steps/{id}/comment")
    public ResponseEntity<InspectionStep> updateComment(
            @PathVariable Long id,
            @RequestBody String newComment) {

        try {
            InspectionStep updated = inspectionStepService.updateComment(id, newComment);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Step löschen.
     * DELETE /api/inspection-steps/{id}
     */
    @DeleteMapping("/inspection-steps/{id}")
    public ResponseEntity<Void> deleteStep(@PathVariable Long id) {
        try {
            inspectionStepService.deleteStep(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
