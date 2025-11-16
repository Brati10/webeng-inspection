package de.dhbw.webenginspection.controller;

import de.dhbw.webenginspection.entity.ChecklistStep;
import de.dhbw.webenginspection.service.ChecklistStepService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class ChecklistStepController {
    
    private final ChecklistStepService checklistStepService;

    public ChecklistStepController(ChecklistStepService checklistStepService) {
        this.checklistStepService = checklistStepService;
    }

    /**
     * Alle Schritte einer Checkliste abrufen (z. B. für Detailansicht).
     * GET /api/checklists/{checklistId}/steps
     */
    @GetMapping("/checklists/{checklistId}/steps")
    public List<ChecklistStep> getStepsForChecklist(@PathVariable Long checklistId) {
        return checklistStepService.getStepsForChecklist(checklistId);
    }

    /**
     * Einzelnen ChecklistStep per ID abrufen.
     * GET /api/checklist-steps/{id}
     */
    @GetMapping("/checklist-steps/{id}")
    public ResponseEntity<ChecklistStep> getStepById(@PathVariable Long id) {
        try {
            ChecklistStep step = checklistStepService.getStepById(id);
            return ResponseEntity.ok(step);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Neuen Schritt für eine Checkliste anlegen.
     * POST /api/checklists/{checklistId}/steps
     */
    @PostMapping("/checklists/{checklistId}/steps")
    public ResponseEntity<ChecklistStep> createStep(
            @PathVariable Long checklistId,
            @RequestBody ChecklistStep step) {

        try {
            ChecklistStep created = checklistStepService.createStep(checklistId, step);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            // Checklist nicht gefunden
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Vorhandenen Schritt aktualisieren.
     * PUT /api/checklist-steps/{id}
     */
    @PutMapping("/checklist-steps/{id}")
    public ResponseEntity<ChecklistStep> updateStep(
            @PathVariable Long id,
            @RequestBody ChecklistStep updated) {

        try {
            ChecklistStep saved = checklistStepService.updateStep(id, updated);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Schritt löschen.
     * DELETE /api/checklist-steps/{id}
     */
    @DeleteMapping("/checklist-steps/{id}")
    public ResponseEntity<Void> deleteStep(@PathVariable Long id) {
        try {
            checklistStepService.deleteStep(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
