package de.dhbw.webenginspection.service;

import de.dhbw.webenginspection.entity.Checklist;
import de.dhbw.webenginspection.entity.ChecklistStep;
import de.dhbw.webenginspection.repository.ChecklistRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
@Transactional
public class ChecklistService {

    private static final Logger log = LoggerFactory.getLogger(ChecklistService.class);
    
    private final ChecklistRepository checklistRepository;

    public ChecklistService(ChecklistRepository checklistRepository) {
        this.checklistRepository = checklistRepository;
    }

    public List<Checklist> getAllChecklists() {
        return checklistRepository.findAll();
    }

    public Checklist getChecklistById(Long id) {
        return checklistRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Checklist with id " + id + " not found"));
    }

    public Checklist createChecklist(Checklist checklist) {
        log.info("Creating new checklist '{}'", checklist.getName());

        // Bidirektionale Beziehung richtig setzen:
        if (checklist.getSteps() != null) {
            for (ChecklistStep step : checklist.getSteps()) {
                step.setChecklist(checklist);
            }
        }

        Checklist saved = checklistRepository.save(checklist);
        log.info("Created checklist with id {}", saved.getId());

        return saved;
    }

    public Checklist updateChecklist(Long id, Checklist updated) {
        log.info("Updating checklist with id {}", id);
        Checklist existing = getChecklistById(id);

        existing.setName(updated.getName());
        existing.setPlantName(updated.getPlantName());
        existing.setRecommendations(updated.getRecommendations());

        // Steps ersetzen
        existing.getSteps().clear();
        if (updated.getSteps() != null) {
            for (ChecklistStep step : updated.getSteps()) {
                step.setChecklist(existing);
                existing.getSteps().add(step);
            }
        }

        Checklist saved = checklistRepository.save(existing);
        log.info("Updated checklist with id {}", saved.getId());

        return saved;
    }

    public void deleteChecklist(Long id) {
        log.info("Deleting checklist with id {}", id);

        if (!checklistRepository.existsById(id)) {
            log.warn("Checklist with id {} not found for deletion", id);
            throw new IllegalArgumentException("Checklist with id " + id + " not found");
        }
        
        checklistRepository.deleteById(id);
        log.info("Deleted checklist with id {}", id);
    }
}
