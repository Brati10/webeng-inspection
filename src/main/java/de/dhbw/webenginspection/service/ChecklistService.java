package de.dhbw.webenginspection.service;

import de.dhbw.webenginspection.entity.Checklist;
import de.dhbw.webenginspection.entity.ChecklistStep;
import de.dhbw.webenginspection.repository.ChecklistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ChecklistService {
    
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
        // Bidirektionale Beziehung richtig setzen:
        if (checklist.getSteps() != null) {
            for (ChecklistStep step : checklist.getSteps()) {
                step.setChecklist(checklist);
            }
        }

        return checklistRepository.save(checklist);
    }

    public Checklist updateChecklist(Long id, Checklist updated) {
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

        return checklistRepository.save(existing);
    }

    public void deleteChecklist(Long id) {
        Checklist existing = getChecklistById(id);
        checklistRepository.delete(existing);
    }
}
