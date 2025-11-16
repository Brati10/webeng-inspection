package de.dhbw.webenginspection.service;

import de.dhbw.webenginspection.entity.Checklist;
import de.dhbw.webenginspection.entity.ChecklistStep;
import de.dhbw.webenginspection.repository.ChecklistRepository;
import de.dhbw.webenginspection.repository.ChecklistStepRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ChecklistStepService {
    
    private final ChecklistStepRepository checklistStepRepository;
    private final ChecklistRepository checklistRepository;

    public ChecklistStepService(ChecklistStepRepository checklistStepRepository,
                                ChecklistRepository checklistRepository) {
        this.checklistStepRepository = checklistStepRepository;
        this.checklistRepository = checklistRepository;
    }

    /**
     * Alle Schritte einer Checkliste, sortiert nach orderIndex.
     */
    public List<ChecklistStep> getStepsForChecklist(Long checklistId) {
        return checklistStepRepository.findByChecklistIdOrderByOrderIndex(checklistId);
    }

    /**
     * Einzelnen Schritt per ID holen oder IllegalArgumentException werfen.
     */
    public ChecklistStep getStepById(Long id) {
        return checklistStepRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ChecklistStep with id " + id + " not found"));
    }

    /**
     * Neuen Schritt für eine bestehende Checkliste anlegen.
     */
    public ChecklistStep createStep(Long checklistId, ChecklistStep step) {
        Checklist checklist = checklistRepository.findById(checklistId)
                .orElseThrow(() -> new IllegalArgumentException("Checklist with id " + checklistId + " not found"));

        step.setId(null); // Sicherheit: neue Entität
        step.setChecklist(checklist);

        return checklistStepRepository.save(step);
    }

    /**
     * Vorhandenen Schritt aktualisieren (Beschreibung, Requirement, Reihenfolge).
     */
    public ChecklistStep updateStep(Long id, ChecklistStep updated) {
        ChecklistStep existing = getStepById(id);

        existing.setDescription(updated.getDescription());
        existing.setRequirement(updated.getRequirement());
        existing.setOrderIndex(updated.getOrderIndex());

        return checklistStepRepository.save(existing);
    }

    /**
     * Schritt löschen.
     */
    public void deleteStep(Long id) {
        ChecklistStep existing = getStepById(id);
        checklistStepRepository.delete(existing);
    }
}
