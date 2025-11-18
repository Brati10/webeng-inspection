package de.dhbw.webenginspection.service;

import de.dhbw.webenginspection.entity.ChecklistStep;
import de.dhbw.webenginspection.entity.Inspection;
import de.dhbw.webenginspection.entity.InspectionStep;
import de.dhbw.webenginspection.entity.StepStatus;
import de.dhbw.webenginspection.repository.ChecklistStepRepository;
import de.dhbw.webenginspection.repository.InspectionRepository;
import de.dhbw.webenginspection.repository.InspectionStepRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
@Transactional
public class InspectionStepService {
    
    private static final Logger log = LoggerFactory.getLogger(InspectionStepService.class);

    private final InspectionStepRepository inspectionStepRepository;
    private final InspectionRepository inspectionRepository;
    private final ChecklistStepRepository checklistStepRepository;

    public InspectionStepService(InspectionStepRepository inspectionStepRepository,
                                 InspectionRepository inspectionRepository,
                                 ChecklistStepRepository checklistStepRepository) {
        this.inspectionStepRepository = inspectionStepRepository;
        this.inspectionRepository = inspectionRepository;
        this.checklistStepRepository = checklistStepRepository;
    }

    public List<InspectionStep> getStepsForInspection(Long inspectionId) {
        return inspectionStepRepository.findByInspectionId(inspectionId);
    }

    public List<InspectionStep> getStepsForInspectionByStatus(Long inspectionId, StepStatus status) {
        return inspectionStepRepository.findByInspectionIdAndStatus(inspectionId, status);
    }

    public InspectionStep getStepById(Long id) {
        return inspectionStepRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("InspectionStep with id " + id + " not found"));
    }

    /**
     * Neuen Step für eine bestehende Inspection anlegen.
     * Hinweis: InspectionService legt normalerweise alle Steps automatisch an.
     * Dieser Weg ist eher für „manuelle Ergänzungen“ gedacht.
     */
    public InspectionStep createStep(Long inspectionId, Long checklistStepId, InspectionStep stepData) {
        log.info("Creating new step for inspection with id {}", inspectionId);
        
        Inspection inspection = inspectionRepository.findById(inspectionId)
                .orElseThrow(() -> new IllegalArgumentException("Inspection with id " + inspectionId + " not found"));

        ChecklistStep checklistStep = checklistStepRepository.findById(checklistStepId)
                .orElseThrow(() -> new IllegalArgumentException("ChecklistStep with id " + checklistStepId + " not found"));

        // Optional: prüfen, ob checklistStep zur gleichen Checklist gehört wie die Inspection
        // if (!checklistStep.getChecklist().getId().equals(inspection.getChecklist().getId())) {
        //     throw new IllegalArgumentException("ChecklistStep does not belong to the same Checklist as the Inspection");
        // }

        stepData.setId(null);
        stepData.setInspection(inspection);
        stepData.setChecklistStep(checklistStep);

        // Falls beim Erstellen kein Status gesetzt wurde, defaulten:
        if (stepData.getStatus() == null) {
            stepData.setStatus(StepStatus.NOT_APPLICABLE);
        }

        InspectionStep saved = inspectionStepRepository.save(stepData);
        log.info("Created inspection step with id {} for inspection id {}", saved.getId(), inspectionId);

        return saved;
    }

    /**
     * Vollständiges Update eines Steps (Status + Kommentar + photoPath).
     * Die Zuordnung zu Inspection und ChecklistStep wird nicht geändert.
     */
    public InspectionStep updateStep(Long id, InspectionStep updated) {
        log.info("Updating inspection step with id {}", id);

        InspectionStep existing = getStepById(id);

        existing.setStatus(updated.getStatus());
        existing.setComment(updated.getComment());
        existing.setPhotoPath(updated.getPhotoPath());

        InspectionStep saved = inspectionStepRepository.save(existing);
        log.info("Updated inspection step with id {}", saved.getId());

        return saved;
    }

    public InspectionStep updateStatus(Long id, StepStatus newStatus) {
        log.info("Updating status of inspection step with id {}", id);

        InspectionStep existing = getStepById(id);
        existing.setStatus(newStatus);

        InspectionStep saved = inspectionStepRepository.save(existing);
        log.info("Updated status of inspection step with id {}", saved.getId());

        return saved;
    }

    public InspectionStep updateComment(Long id, String newComment) {
        log.info("Updating comment of inspection step with id {}", id);

        InspectionStep existing = getStepById(id);
        existing.setComment(newComment);

        InspectionStep saved = inspectionStepRepository.save(existing);
        log.info("Updated comment of inspection step with id {}", saved.getId());

        return saved;
    }

    public void deleteStep(Long id) {
        log.info("Deleting inspection step with id {}", id);

        if(!inspectionStepRepository.existsById(id)) {
            log.warn("InspectionStep with id {} not found for deletion", id);
            throw new IllegalArgumentException("InspectionStep with id " + id + " not found");
        }
        
        inspectionStepRepository.deleteById(id);
        log.info("Deleted inspection step with id {}", id);
    }
}
