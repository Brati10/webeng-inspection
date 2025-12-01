package de.dhbw.webenginspection.controller;

import de.dhbw.webenginspection.dto.InspectionCreateRequest;
import de.dhbw.webenginspection.entity.Inspection;
import de.dhbw.webenginspection.service.InspectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger log = LoggerFactory.getLogger(InspectionController.class);

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
    @PreAuthorize("hasRole('ADMIN')")
    public List<Inspection> getAll() {
        log.info("Fetching all inspections");
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
    @PreAuthorize("hasAnyRole('ADMIN', 'INSPECTOR')")
    public ResponseEntity<Inspection> getById(@PathVariable
    Long id) {
        log.info("Fetching inspection with id {}", id);
        return inspectionService.getInspectionById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Liefert alle Inspektionen, die einem bestimmten Benutzer
     * (verantwortlicher Mitarbeiter) zugeordnet sind.
     *
     * Beispiel: GET /api/inspections/by-user/1
     *
     * @param userId ID des Benutzers
     * @return Liste der Inspektionen des Users (ggf. leer, aber niemals
     * {@code null})
     */
    @GetMapping("/by-user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @userService.isCurrentUser(#userId)")
    public ResponseEntity<List<Inspection>> getByUser(@PathVariable
    Long userId) {
        log.info("Fetching inspections for user with id {}", userId);
        List<Inspection> inspections = inspectionService.getInspectionsForUser(userId);
        return ResponseEntity.ok(inspections);
    }

    /**
     * Erstellt eine neue Inspection auf Basis einer bestehenden Checklist. Die
     * Details werden aus dem Request gelesen, die verknüpfte Checklist wird
     * über deren ID ermittelt (nur für Admins).
     *
     * @param request die Daten zur Erstellung der Inspection, inklusive
     * Checklist-ID
     * @return {@code 201 Created} mit der erstellten {@link Inspection} oder
     * {@code 400 Bad Request}, wenn die referenzierte Checklist nicht existiert
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Inspection> create(@Valid
    @RequestBody
    InspectionCreateRequest request) {
        log.info("Creating inspection for checklist {}", request.getChecklistId());
        try {
            Inspection created = inspectionService.createInspectionFromChecklist(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            log.error("Error creating inspection: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Aktualisiert den Status einer bestehenden Inspection.
     *
     * @param id die ID der zu aktualisierenden Inspection
     * @param newStatus der neue Statuswert als String
     * @return {@code 200 OK} mit der aktualisierten {@link Inspection} oder
     * {@code 400 Bad Request}, wenn der Status ungültig ist, oder
     * {@code 404 Not Found}, wenn keine Inspection mit der ID existiert
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSPECTOR')")
    public ResponseEntity<Inspection> updateStatus(@PathVariable
    Long id, @RequestBody
    String newStatus) {
        log.info("Updating status of inspection with id {} to {}", id, newStatus);
        try {
            Inspection updated = inspectionService.updateStatus(id, newStatus);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            log.error("Error updating inspection status: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Löscht eine Inspection anhand ihrer ID (nur für Admins).
     *
     * @param id die ID der zu löschenden Inspection
     * @return {@code 204 No Content} bei erfolgreicher Löschung oder
     * {@code 404 Not Found}, wenn keine Inspection mit der ID existiert
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable
    Long id) {
        log.info("Deleting inspection with id {}", id);
        try {
            inspectionService.deleteInspection(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Error deleting inspection: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}