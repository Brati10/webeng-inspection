package de.dhbw.webenginspection.controller;

import de.dhbw.webenginspection.dto.InspectionCreateRequest;
import de.dhbw.webenginspection.entity.Inspection;
import de.dhbw.webenginspection.service.InspectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * REST-Controller für die Verwaltung von {@link Inspection}-Entitäten. Bietet
 * Endpunkte zum Erstellen, Abrufen, Löschen und zur Statusänderung von
 * Inspektionen. Wird von einem React-Frontend (Standard-Port 5173) konsumiert.
 */
@RestController
@RequestMapping("/api/inspections")
@CrossOrigin(origins = "http://localhost:5173") // für React-Frontend
public class InspectionController {

    private final InspectionService inspectionService;

    public InspectionController(InspectionService inspectionService) {
        this.inspectionService = inspectionService;
    }

    /**
     * Gibt alle vorhandenen Inspektionen zurück.
     *
     * @return eine Liste aller {@link Inspection}-Entitäten
     */
    @GetMapping
    public List<Inspection> getAll() {
        return inspectionService.getAllInspections();
    }

    /**
     * Gibt eine einzelne Inspection anhand ihrer ID zurück.
     *
     * @param id die ID der gewünschten Inspection
     * @return {@code 200 OK} mit der Inspection oder {@code 404 Not Found},
     * falls keine Inspection mit der angegebenen ID existiert
     */
    @GetMapping("/{id}")
    public ResponseEntity<Inspection> getById(@PathVariable
    Long id) {
        try {
            Inspection inspection = inspectionService.getInspectionById(id);
            return ResponseEntity.ok(inspection);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Erstellt eine neue Inspection auf Basis einer bestehenden Checklist. Die
     * Details werden aus dem Request gelesen, die verknüpfte Checklist wird
     * über deren ID ermittelt.
     *
     * @param request die Daten zur Erstellung der Inspection, inklusive
     * Checklist-ID
     * @return {@code 201 Created} mit der erstellten {@link Inspection} oder
     * {@code 400 Bad Request}, wenn die referenzierte Checklist nicht existiert
     */
    @PostMapping
    public ResponseEntity<Inspection> create(@Valid
    @RequestBody
    InspectionCreateRequest request) {
        try {
            Inspection created = inspectionService.createInspectionFromChecklist(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Aktualisiert den Status einer bestehenden Inspection.
     *
     * @param id die ID der zu aktualisierenden Inspection
     * @param newStatus der neue Statuswert als String
     * @return {@code 200 OK} mit der aktualisierten {@link Inspection} oder
     * {@code 404 Not Found}, wenn keine Inspection mit der ID existiert
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Inspection> updateStatus(@PathVariable
    Long id, @RequestBody
    String newStatus) {
        try {
            Inspection updated = inspectionService.updateStatus(id, newStatus);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Löscht eine Inspection anhand ihrer ID.
     *
     * @param id die ID der zu löschenden Inspection
     * @return {@code 204 No Content} bei erfolgreicher Löschung oder
     * {@code 404 Not Found}, wenn keine Inspection mit der ID existiert
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable
    Long id) {
        try {
            inspectionService.deleteInspection(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
