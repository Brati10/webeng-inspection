package de.dhbw.webenginspection.controller;

import de.dhbw.webenginspection.entity.Checklist;
import de.dhbw.webenginspection.service.ChecklistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * REST-Controller für die Verwaltung von {@link Checklist}-Entitäten. Bietet
 * Endpunkte zum Erstellen, Abrufen, Aktualisieren und Löschen von Checklisten.
 */
@RestController
@RequestMapping("/api/checklists")
@CrossOrigin(origins = "http://localhost:5173")
public class ChecklistController {

    private static final Logger log = LoggerFactory.getLogger(ChecklistController.class);

    private final ChecklistService checklistService;

    public ChecklistController(ChecklistService checklistService) {
        this.checklistService = checklistService;
    }

    /**
     * Gibt alle vorhandenen Checklisten zurück (authentifiziert).
     *
     * @return eine Liste aller {@link Checklist}-Entitäten
     */
    @GetMapping
    @PreAuthorize("authenticated")
    public List<Checklist> getAll() {
        log.info("Fetching all checklists");
        return checklistService.getAllChecklists();
    }

    /**
     * Gibt eine einzelne Checklist anhand ihrer ID zurück (authentifiziert).
     *
     * @param id die ID der gewünschten Checklist
     * @return {@code 200 OK} mit der Checklist oder {@code 404 Not Found}
     */
    @GetMapping("/{id}")
    @PreAuthorize("authenticated")
    public ResponseEntity<Checklist> getById(@PathVariable
    Long id) {
        log.info("Fetching checklist with id {}", id);
        return checklistService.getChecklistById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Erstellt eine neue Checklist (nur für Admins).
     *
     * @param checklist die Daten der zu erstellenden {@link Checklist}
     * @return {@code 201 Created} mit der gespeicherten Checklist
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Checklist> create(@RequestBody
    Checklist checklist) {
        log.info("Creating new checklist '{}'", checklist.getName());
        Checklist created = checklistService.createChecklist(checklist);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Aktualisiert eine bestehende Checklist (nur für Admins).
     *
     * @param id die ID der zu aktualisierenden Checklist
     * @param checklist die neuen Daten für die Checklist
     * @return {@code 200 OK} mit der aktualisierten Checklist
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Checklist> update(@PathVariable
    Long id, @RequestBody
    Checklist checklist) {
        log.info("Updating checklist with id {}", id);
        try {
            Checklist updated = checklistService.updateChecklist(id, checklist);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            log.error("Error updating checklist: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Löscht eine Checklist anhand ihrer ID (nur für Admins).
     *
     * @param id die ID der zu löschenden Checklist
     * @return {@code 204 No Content} bei erfolgreicher Löschung
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable
    Long id) {
        log.info("Deleting checklist with id {}", id);
        try {
            checklistService.deleteChecklist(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Error deleting checklist: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}