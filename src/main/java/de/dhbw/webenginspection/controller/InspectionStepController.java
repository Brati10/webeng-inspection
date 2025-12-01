package de.dhbw.webenginspection.controller;

import de.dhbw.webenginspection.entity.InspectionStep;
import de.dhbw.webenginspection.entity.StepStatus;
import de.dhbw.webenginspection.service.InspectionStepService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * REST-Controller zur Verwaltung von {@link InspectionStep}-Entitäten. Bietet
 * Endpunkte zum Abrufen, Filtern, Erstellen, Aktualisieren und Löschen
 * einzelner Schritte innerhalb einer Inspection.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class InspectionStepController {

    private static final Logger log = LoggerFactory.getLogger(InspectionStepController.class);

    private final InspectionStepService inspectionStepService;

    public InspectionStepController(InspectionStepService inspectionStepService) {
        this.inspectionStepService = inspectionStepService;
    }

    /**
     * Gibt alle Schritte einer bestimmten Inspection zurück.
     *
     * @param inspectionId die ID der Inspection
     * @return eine Liste aller zugehörigen {@link InspectionStep}-Entitäten
     */
    @GetMapping("/inspections/{inspectionId}/steps")
    @PreAuthorize("authenticated")
    public List<InspectionStep> getStepsForInspection(@PathVariable
    Long inspectionId) {
        log.info("Fetching all steps for inspection with id {}", inspectionId);
        return inspectionStepService.getStepsForInspection(inspectionId);
    }

    /**
     * Gibt alle Schritte einer Inspection zurück, gefiltert nach einem
     * bestimmten Status. Der Status muss einem gültigen
     * {@link StepStatus}-Enum-Wert entsprechen.
     *
     * @param inspectionId die ID der Inspection
     * @param status der gewünschte Status als String
     * @return {@code 200 OK} mit einer gefilterten Liste oder
     * {@code 400 Bad Request}, wenn der Status ungültig ist
     */
    @GetMapping("/inspections/{inspectionId}/steps/status/{status}")
    @PreAuthorize("authenticated")
    public ResponseEntity<List<InspectionStep>> getStepsForInspectionByStatus(@PathVariable
    Long inspectionId, @PathVariable
    String status) {
        log.info("Fetching steps for inspection {} with status {}", inspectionId, status);

        try {
            StepStatus stepStatus = StepStatus.valueOf(status.toUpperCase());
            List<InspectionStep> steps = inspectionStepService.getStepsForInspectionByStatus(inspectionId, stepStatus);
            return ResponseEntity.ok(steps);
        } catch (IllegalArgumentException e) {
            log.error("Invalid status value: {}", status);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Gibt einen einzelnen InspectionStep anhand seiner ID zurück.
     *
     * @param stepId die ID des Steps
     * @return {@code 200 OK} mit dem Step oder {@code 404 Not Found}
     */
    @GetMapping("/inspection-steps/{stepId}")
    @PreAuthorize("authenticated")
    public ResponseEntity<InspectionStep> getStepById(@PathVariable
    Long stepId) {
        log.info("Fetching inspection step with id {}", stepId);
        return inspectionStepService.getStepById(stepId).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Aktualisiert den Status eines InspectionStep.
     *
     * @param stepId die ID des Steps
     * @param newStatus der neue Status als String
     * @return {@code 200 OK} mit dem aktualisierten Step,
     * {@code 400 Bad Request}, wenn der Status ungültig ist, oder
     * {@code 404 Not Found}, wenn der Step nicht existiert
     */
    @PatchMapping("/inspection-steps/{stepId}/status")
    @PreAuthorize("authenticated")
    public ResponseEntity<InspectionStep> updateStatus(@PathVariable
    Long stepId, @RequestBody
    String newStatus) {
        log.info("Updating status of step {} to {}", stepId, newStatus);

        try {
            StepStatus stepStatus = StepStatus.valueOf(newStatus.toUpperCase());
            InspectionStep updated = inspectionStepService.updateStatus(stepId, stepStatus);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            log.error("Error updating step status: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Aktualisiert den Kommentar eines InspectionStep.
     *
     * @param stepId die ID des Steps
     * @param newComment der neue Kommentar
     * @return {@code 200 OK} mit dem aktualisierten Step oder
     * {@code 404 Not Found}, wenn der Step nicht existiert
     */
    @PatchMapping("/inspection-steps/{stepId}/comment")
    @PreAuthorize("authenticated")
    public ResponseEntity<InspectionStep> updateComment(@PathVariable
    Long stepId, @RequestBody
    String newComment) {
        log.info("Updating comment of step {}", stepId);
        try {
            InspectionStep updated = inspectionStepService.updateComment(stepId, newComment);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            log.error("Error updating step comment: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Löscht einen InspectionStep.
     *
     * @param stepId die ID des Steps
     * @return {@code 204 No Content} bei erfolgreicher Löschung oder
     * {@code 404 Not Found}, wenn der Step nicht existiert
     */
    @DeleteMapping("/inspection-steps/{stepId}")
    @PreAuthorize("authenticated")
    public ResponseEntity<Void> deleteStep(@PathVariable
    Long stepId) {
        log.info("Deleting inspection step with id {}", stepId);
        try {
            inspectionStepService.deleteStep(stepId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Error deleting step: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}