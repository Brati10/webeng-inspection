package de.dhbw.webenginspection.service;

import de.dhbw.webenginspection.dto.InspectionCreateRequest;
import de.dhbw.webenginspection.entity.Checklist;
import de.dhbw.webenginspection.entity.ChecklistStep;
import de.dhbw.webenginspection.entity.Inspection;
import de.dhbw.webenginspection.entity.InspectionStep;
import de.dhbw.webenginspection.entity.StepStatus;
import de.dhbw.webenginspection.repository.ChecklistRepository;
import de.dhbw.webenginspection.repository.InspectionRepository;
import de.dhbw.webenginspection.entity.User;
import de.dhbw.webenginspection.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service zur Verwaltung von {@link Inspection}-Entitäten. Bietet Funktionen
 * zum Abrufen, Erstellen und Löschen von Inspektionen sowie zur Aktualisierung
 * ihres Status. Beim Erstellen einer Inspektion werden alle Schritte der
 * zugrunde liegenden Checklist als {@link InspectionStep} initialisiert.
 */
@Service
@Transactional
public class InspectionService {

    private static final Logger log = LoggerFactory.getLogger(InspectionService.class);

    private final InspectionRepository inspectionRepository;

    private final ChecklistRepository checklistRepository;

    private final UserRepository userRepository;

    public InspectionService(InspectionRepository inspectionRepository, ChecklistRepository checklistRepository,
            UserRepository userRepository) {
        this.inspectionRepository = inspectionRepository;
        this.checklistRepository = checklistRepository;
        this.userRepository = userRepository;
    }

    public List<Inspection> getAllInspections() {
        return inspectionRepository.findAll();
    }

    /**
     * Gibt die Inspection mit der angegebenen ID zurück.
     *
     * @param id die eindeutige ID der gesuchten Inspection
     * @return die gefundene {@link Inspection}
     * @throws IllegalArgumentException wenn keine Inspection mit der ID
     * existiert
     */
    public Inspection getInspectionById(Long id) {
        return inspectionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inspection with id " + id + " not found"));
    }

    /**
     * Erstellt eine neue {@link Inspection} auf Basis einer bestehenden
     * {@link Checklist}. Dabei werden alle
     * {@link ChecklistStep}-Vorlagenschritte der Checklist in entsprechende
     * {@link InspectionStep}-Instanzen übertragen und der Inspection
     * zugeordnet. Felder wie Titel, Anlagenname und Datum werden aus dem
     * Request übernommen oder – falls nicht vorhanden – aus der Checklist bzw.
     * der aktuellen Systemzeit abgeleitet.
     *
     * @param request Daten zur Erstellung der Inspection, einschließlich der ID
     * der zugrunde liegenden Checklist
     * @return die erstellte und gespeicherte {@link Inspection}
     * @throws IllegalArgumentException wenn keine Checklist mit der angegebenen
     * ID existiert
     */
    public Inspection createInspectionFromChecklist(InspectionCreateRequest request) {
        log.info("Creating inspection for checklist {} at plant '{}'", request.getChecklistId(),
                request.getPlantName());
        Checklist checklist = checklistRepository.findById(request.getChecklistId()).orElseThrow(
                () -> new IllegalArgumentException("Checklist with id " + request.getChecklistId() + " not found"));

        Inspection inspection = new Inspection();
        inspection.setChecklist(checklist);

        inspection.setTitle(request.getTitle() != null ? request.getTitle() : checklist.getName());
        inspection.setPlantName(request.getPlantName() != null ? request.getPlantName() : checklist.getPlantName());
        inspection.setInspectionDate(
                request.getInspectionDate() != null ? request.getInspectionDate() : LocalDateTime.now());
        inspection.setStatus("PLANNED");
        inspection.setGeneralComment(request.getGeneralComment());

        Long responsibleUserId = request.getResponsibleUserId();
        User responsibleUser = userRepository.findById(responsibleUserId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + responsibleUserId + " not found"));

        inspection.setAssignedInspector(responsibleUser);

        // Steps aus der Checklist kopieren
        if (checklist.getSteps() != null) {
            for (ChecklistStep templateStep : checklist.getSteps()) {
                InspectionStep inspectionStep = new InspectionStep();
                inspectionStep.setChecklistStep(templateStep);
                inspectionStep.setStatus(StepStatus.NOT_APPLICABLE); // Initialstatus
                inspectionStep.setComment(null);
                inspectionStep.setPhotoPath(null);

                inspection.addStep(inspectionStep); // setzt auch inspection im
                                                    // Step
            }
        }

        Inspection saved = inspectionRepository.save(inspection);
        log.info("Created inspection with id {} for checklist {}", saved.getId(), checklist.getId());

        return saved;
    }

    /**
     * Aktualisiert den Status einer bestehenden Inspection.
     *
     * @param id die ID der zu aktualisierenden Inspection
     * @param newStatus der neue Statuswert
     * @return die aktualisierte {@link Inspection}
     * @throws IllegalArgumentException wenn keine Inspection mit der ID
     * existiert
     */
    public Inspection updateStatus(Long id, String newStatus) {
        log.info("Updating status of inspection with id {} to {}", id, newStatus);

        Inspection inspection = getInspectionById(id);
        inspection.setStatus(newStatus);

        Inspection saved = inspectionRepository.save(inspection);
        log.info("Updated status of inspection with id {} to {}", saved.getId(), newStatus);

        return saved;
    }

    /**
     * Löscht die Inspection mit der angegebenen ID.
     *
     * @param id die ID der zu löschenden Inspection
     * @throws IllegalArgumentException wenn keine Inspection mit der ID
     * existiert
     */
    public void deleteInspection(Long id) {
        log.info("Deleting inspection with id {}", id);

        if (!inspectionRepository.existsById(id)) {
            log.warn("Inspection with id {} not found for deletion", id);
            throw new IllegalArgumentException("Inspection with id " + id + " not found");
        }

        inspectionRepository.deleteById(id);
        log.info("Deleted inspection with id {}", id);
    }
}
