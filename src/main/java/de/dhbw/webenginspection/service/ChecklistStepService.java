package de.dhbw.webenginspection.service;

import de.dhbw.webenginspection.entity.Checklist;
import de.dhbw.webenginspection.entity.ChecklistStep;
import de.dhbw.webenginspection.repository.ChecklistRepository;
import de.dhbw.webenginspection.repository.ChecklistStepRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
@Transactional
public class ChecklistStepService {
    
    private static final Logger log = LoggerFactory.getLogger(ChecklistStepService.class);

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
        log.info("Creating new step for checklist with id {}", checklistId);
        Checklist checklist = checklistRepository.findById(checklistId)
                .orElseThrow(() -> new IllegalArgumentException("Checklist with id " + checklistId + " not found"));

        step.setId(null); // Sicherheit: neue Entität
        step.setChecklist(checklist);

        ChecklistStep saved = checklistStepRepository.save(step);
        log.info("Created checklist step with id {} for checklist id {}", saved.getId(), checklistId);

        return saved;
    }

    /**
     * Vorhandenen Schritt aktualisieren (Beschreibung, Requirement, Reihenfolge).
     */
    public ChecklistStep updateStep(Long id, ChecklistStep updated) {
        log.info("Updating checklist step with id {}", id);
        ChecklistStep existing = getStepById(id);

        existing.setDescription(updated.getDescription());
        existing.setRequirement(updated.getRequirement());
        existing.setOrderIndex(updated.getOrderIndex());

        ChecklistStep saved = checklistStepRepository.save(existing);
        log.info("Updated checklist step with id {}", saved.getId());

        return saved;
    }

    /**
     * Schritt löschen.
     */
    public void deleteStep(Long id) {
        log.info("Deleting checklist step with id {}", id);

        if(!checklistStepRepository.existsById(id)) {
            log.warn("ChecklistStep with id {} not found for deletion", id);
            throw new IllegalArgumentException("ChecklistStep with id " + id + " not found");
        }

        checklistStepRepository.deleteById(id);
        log.info("Deleted checklist step with id {}", id);
    }
}
