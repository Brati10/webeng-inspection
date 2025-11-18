package de.dhbw.webenginspection.service;

import de.dhbw.webenginspection.dto.InspectionCreateRequest;
import de.dhbw.webenginspection.entity.Checklist;
import de.dhbw.webenginspection.entity.ChecklistStep;
import de.dhbw.webenginspection.entity.Inspection;
import de.dhbw.webenginspection.entity.InspectionStep;
import de.dhbw.webenginspection.entity.StepStatus;
import de.dhbw.webenginspection.repository.ChecklistRepository;
import de.dhbw.webenginspection.repository.InspectionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class InspectionService {

    private static final Logger log = LoggerFactory.getLogger(InspectionService.class);
    
    private final InspectionRepository inspectionRepository;
    private final ChecklistRepository checklistRepository;

    public InspectionService(InspectionRepository inspectionRepository,
                             ChecklistRepository checklistRepository) {
        this.inspectionRepository = inspectionRepository;
        this.checklistRepository = checklistRepository;
    }

    public List<Inspection> getAllInspections() {
        return inspectionRepository.findAll();
    }

    public Inspection getInspectionById(Long id) {
        return inspectionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inspection with id " + id + " not found"));
    }

    /**
     * Erzeugt eine neue Inspection auf Basis einer bestehenden Checklist.
     * Alle ChecklistSteps werden in InspectionSteps kopiert.
     */
    public Inspection createInspectionFromChecklist(InspectionCreateRequest request) {
        log.info("Creating inspection for checklist {} at plant '{}'",
             request.getChecklistId(), request.getPlantName());
        Checklist checklist = checklistRepository.findById(request.getChecklistId())
                .orElseThrow(() -> new IllegalArgumentException("Checklist with id " + request.getChecklistId() + " not found"));

        Inspection inspection = new Inspection();
        inspection.setChecklist(checklist);

        inspection.setTitle(request.getTitle() != null ? request.getTitle() : checklist.getName());
        inspection.setPlantName(request.getPlantName() != null ? request.getPlantName() : checklist.getPlantName());
        inspection.setInspectionDate(request.getInspectionDate() != null ? request.getInspectionDate() : LocalDateTime.now());
        inspection.setStatus("PLANNED");
        inspection.setGeneralComment(request.getGeneralComment());

        // Steps aus der Checklist kopieren
        if (checklist.getSteps() != null) {
            for (ChecklistStep templateStep : checklist.getSteps()) {
                InspectionStep inspectionStep = new InspectionStep();
                inspectionStep.setChecklistStep(templateStep);
                inspectionStep.setStatus(StepStatus.NOT_APPLICABLE); // Initialstatus
                inspectionStep.setComment(null);
                inspectionStep.setPhotoPath(null);

                inspection.addStep(inspectionStep); // setzt auch inspection im Step
            }
        }
        
        Inspection saved = inspectionRepository.save(inspection);
        log.info("Created inspection with id {} for checklist {}", saved.getId(), checklist.getId());

        return saved;
    }

    public void deleteInspection(Long id) {
        log.info("Deleting inspection with id {}", id);

        if (!inspectionRepository.existsById(id)) {
            log.warn("Inspection with id {} not found for deletion", id);
            throw new IllegalArgumentException("Inspection with id " + id + " not found");
        }
        
        inspectionRepository.deleteById(id);
        log.info("Deleted inspection with id {}", id);
    }

    // einfache Status-Änderung (optional, kann man später ausbauen)
    public Inspection updateStatus(Long id, String newStatus) {
        log.info("Updating status of inspection with id {} to {}", id, newStatus);
        
        Inspection inspection = getInspectionById(id);
        inspection.setStatus(newStatus);

        Inspection saved = inspectionRepository.save(inspection);
        log.info("Updated status of inspection with id {} to {}", saved.getId(), newStatus);
        
        return saved;
    }
}
