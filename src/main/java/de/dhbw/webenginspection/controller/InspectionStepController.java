package de.dhbw.webenginspection.controller;

import de.dhbw.webenginspection.entity.InspectionStep;
import de.dhbw.webenginspection.entity.StepStatus;
import de.dhbw.webenginspection.service.InspectionStepService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public List<InspectionStep> getStepsForInspection(@PathVariable
    Long inspectionId) {
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
    public ResponseEntity<List<InspectionStep>> getStepsForInspectionByStatus(@PathVariable
    Long inspectionId, @PathVariable
    String status) {

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
     * Gibt einen einzelnen InspectionStep anhand seiner ID zurück.
     *
     * @param id die ID des gesuchten Schritts
     * @return {@code 200 OK} mit dem Schritt oder {@code 404 Not Found}, wenn
     * kein Schritt mit der ID existiert
     */
    @GetMapping("/inspection-steps/{id}")
    public ResponseEntity<InspectionStep> getStepById(@PathVariable
    Long id) {
        try {
            InspectionStep step = inspectionStepService.getStepById(id);
            return ResponseEntity.ok(step);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Erstellt einen neuen Schritt für eine bestehende Inspection. Der Schritt
     * wird sowohl mit der Inspection als auch mit dem zugrunde liegenden
     * ChecklistStep verknüpft.
     *
     * @param inspectionId die ID der Inspection, zu der der Schritt gehört
     * @param checklistStepId die ID des zugrunde liegenden ChecklistSteps
     * @param stepData die Daten für den neuen {@link InspectionStep}
     * @return {@code 201 Created} mit dem erstellten Schritt oder
     * {@code 404 Not Found}, wenn Inspection oder ChecklistStep nicht
     * existieren
     */
    @PostMapping("/inspections/{inspectionId}/steps")
    public ResponseEntity<InspectionStep> createStep(@PathVariable
    Long inspectionId, @RequestParam
    Long checklistStepId, @RequestBody
    InspectionStep stepData) {

        try {
            InspectionStep created = inspectionStepService.createStep(inspectionId, checklistStepId, stepData);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            // Inspection oder ChecklistStep nicht gefunden / fachlicher Fehler
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Aktualisiert einen bestehenden InspectionStep vollständig. Verändert
     * Status, Kommentar und photoPath.
     *
     * @param id die ID des zu aktualisierenden Schritts
     * @param updated ein Objekt mit den neuen Werten
     * @return {@code 200 OK} mit dem aktualisierten Schritt oder
     * {@code 404 Not Found}, wenn der Schritt nicht existiert
     */
    @PutMapping("/inspection-steps/{id}")
    public ResponseEntity<InspectionStep> updateStep(@PathVariable
    Long id, @RequestBody
    InspectionStep updated) {

        try {
            InspectionStep saved = inspectionStepService.updateStep(id, updated);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Aktualisiert nur den Status eines bestehenden InspectionStep. Der Status
     * muss einem gültigen {@link StepStatus}-Enum-Wert entsprechen.
     *
     * @param id die ID des zu aktualisierenden Schritts
     * @param newStatus der neue Status als String
     * @return {@code 200 OK} mit dem aktualisierten Schritt oder
     * {@code 400 Bad Request}, wenn der Status ungültig ist
     */
    @PatchMapping("/inspection-steps/{id}/status")
    public ResponseEntity<InspectionStep> updateStatus(@PathVariable
    Long id, @RequestBody
    String newStatus) {

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
     * Aktualisiert ausschließlich den Kommentar eines bestehenden Schritts.
     *
     * @param id die ID des zu aktualisierenden Schritts
     * @param newComment der neue Kommentartext
     * @return {@code 200 OK} mit dem aktualisierten Schritt oder
     * {@code 404 Not Found}, wenn der Schritt nicht existiert
     */
    @PatchMapping("/inspection-steps/{id}/comment")
    public ResponseEntity<InspectionStep> updateComment(@PathVariable
    Long id, @RequestBody
    String newComment) {

        try {
            InspectionStep updated = inspectionStepService.updateComment(id, newComment);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Löscht einen bestehenden InspectionStep anhand seiner ID.
     *
     * @param id die ID des zu löschenden Schritts
     * @return {@code 204 No Content} bei Erfolg oder {@code 404 Not Found},
     * wenn kein Schritt mit der ID existiert
     */
    @DeleteMapping("/inspection-steps/{id}")
    public ResponseEntity<Void> deleteStep(@PathVariable
    Long id) {
        try {
            inspectionStepService.deleteStep(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
