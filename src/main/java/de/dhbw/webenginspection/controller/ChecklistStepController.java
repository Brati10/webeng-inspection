package de.dhbw.webenginspection.controller;

import de.dhbw.webenginspection.entity.ChecklistStep;
import de.dhbw.webenginspection.service.ChecklistStepService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * REST-Controller zum Verwalten von {@link ChecklistStep}-Entitäten. Bietet
 * Endpunkte zum Abrufen, Erstellen, Aktualisieren und Löschen von Schritten
 * innerhalb einer Checkliste.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class ChecklistStepController {

    private static final Logger log = LoggerFactory.getLogger(ChecklistStepController.class);

    private final ChecklistStepService checklistStepService;

    public ChecklistStepController(ChecklistStepService checklistStepService) {
        this.checklistStepService = checklistStepService;
    }

    /**
     * Gibt alle Schritte einer bestimmten Checkliste zurück.
     *
     * @param checklistId die ID der Checkliste
     * @return eine Liste aller zugehörigen {@link ChecklistStep}-Entitäten
     */
    @GetMapping("/checklists/{checklistId}/steps")
    public List<ChecklistStep> getStepsForChecklist(@PathVariable Long checklistId) {
        log.info("Fetching steps for checklist with id {}", checklistId);
        return checklistStepService.getStepsForChecklist(checklistId);
    }

    /**
     * Gibt einen einzelnen ChecklistStep anhand seiner ID zurück.
     *
     * @param id die ID des gewünschten Schritts
     * @return {@code 200 OK} mit dem Schritt oder {@code 404 Not Found}, falls
     * kein Schritt mit der ID existiert
     */
    @GetMapping("/checklist-steps/{id}")
    public ResponseEntity<ChecklistStep> getStepById(@PathVariable Long id) {
        log.info("Fetching checklist step with id {}", id);
        return checklistStepService.getStepById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Erstellt einen neuen Schritt für eine bestehende Checkliste.
     *
     * @param checklistId die ID der Checkliste, zu der der Schritt gehört
     * @param step die zu erstellende {@link ChecklistStep}-Entität
     * @return {@code 201 Created} mit dem neu erstellten Schritt oder
     * {@code 404 Not Found}, wenn die Checkliste nicht existiert
     */
    @PostMapping("/checklists/{checklistId}/steps")
    public ResponseEntity<ChecklistStep> createStep(@PathVariable Long checklistId,
            @RequestBody ChecklistStep step) {
        log.info("Creating new step for checklist with id {}", checklistId);
        
        try {
            ChecklistStep created = checklistStepService.createStep(checklistId, step);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            log.error("Error creating checklist step: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Aktualisiert einen bestehenden Schritt einer Checkliste.
     *
     * @param id die ID des zu aktualisierenden Schritts
     * @param updated ein Objekt mit den neuen Werten (Beschreibung,
     * Requirement, orderIndex)
     * @return {@code 200 OK} mit dem aktualisierten Schritt oder
     * {@code 404 Not Found}, wenn der Schritt nicht existiert
     */
    @PutMapping("/checklist-steps/{id}")
    public ResponseEntity<ChecklistStep> updateStep(@PathVariable Long id,
            @RequestBody ChecklistStep updated) {
        log.info("Updating checklist step with id {}", id);
        
        try {
            ChecklistStep saved = checklistStepService.updateStep(id, updated);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            log.error("Error updating checklist step: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Löscht einen bestehenden Schritt anhand seiner ID.
     *
     * @param id die ID des zu löschenden Schritts
     * @return {@code 204 No Content} bei Erfolg oder {@code 404 Not Found},
     * wenn der Schritt nicht existiert
     */
    @DeleteMapping("/checklist-steps/{id}")
    public ResponseEntity<Void> deleteStep(@PathVariable Long id) {
        log.info("Deleting checklist step with id {}", id);
        
        try {
            checklistStepService.deleteStep(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Error deleting checklist step: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}