package de.dhbw.webenginspection.controller;

import de.dhbw.webenginspection.entity.Checklist;
import de.dhbw.webenginspection.service.ChecklistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checklists")
@CrossOrigin(origins = "http://localhost:5173") // später für dein React-Frontend (Vite default-Port)
public class ChecklistController {
    
    private final ChecklistService checklistService;

    public ChecklistController(ChecklistService checklistService) {
        this.checklistService = checklistService;
    }

    @GetMapping
    public List<Checklist> getAll() {
        return checklistService.getAllChecklists();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Checklist> getById(@PathVariable Long id) {
        try {
            Checklist checklist = checklistService.getChecklistById(id);
            return ResponseEntity.ok(checklist);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Checklist> create(@RequestBody Checklist checklist) {
        Checklist created = checklistService.createChecklist(checklist);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Checklist> update(@PathVariable Long id, @RequestBody Checklist checklist) {
        try {
            Checklist updated = checklistService.updateChecklist(id, checklist);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            checklistService.deleteChecklist(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
