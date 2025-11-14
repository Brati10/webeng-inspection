package de.dhbw.webenginspection.controller;

import de.dhbw.webenginspection.dto.InspectionCreateRequest;
import de.dhbw.webenginspection.entity.Inspection;
import de.dhbw.webenginspection.service.InspectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inspections")
@CrossOrigin(origins = "http://localhost:5173") // für React-Frontend
public class InspectionController {
    
    private final InspectionService inspectionService;

    public InspectionController(InspectionService inspectionService) {
        this.inspectionService = inspectionService;
    }

    @GetMapping
    public List<Inspection> getAll() {
        return inspectionService.getAllInspections();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inspection> getById(@PathVariable Long id) {
        try {
            Inspection inspection = inspectionService.getInspectionById(id);
            return ResponseEntity.ok(inspection);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Erzeugt eine Inspection auf Basis einer Checklist.
     */
    @PostMapping
    public ResponseEntity<Inspection> create(@RequestBody InspectionCreateRequest request) {
        try {
            Inspection created = inspectionService.createInspectionFromChecklist(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            inspectionService.deleteInspection(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // optional: Status-Update-Endpunkt
    @PatchMapping("/{id}/status")
    public ResponseEntity<Inspection> updateStatus(@PathVariable Long id, @RequestBody String newStatus) {
        try {
            Inspection updated = inspectionService.updateStatus(id, newStatus);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
