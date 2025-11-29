package de.dhbw.webenginspection.service;

import de.dhbw.webenginspection.dto.InspectionCreateRequest;
import de.dhbw.webenginspection.entity.Checklist;
import de.dhbw.webenginspection.entity.ChecklistStep;
import de.dhbw.webenginspection.entity.Inspection;
import de.dhbw.webenginspection.entity.InspectionStatus;
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

import java.util.List;
import java.util.Optional;

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

    /**
     * Gibt alle vorhandenen Inspektionen zurück.
     *
     * @return eine Liste aller {@link Inspection}-Entitäten (niemals {@code null})
     */
    public List<Inspection> getAllInspections() {
        log.info("Fetching all inspections");
        return inspectionRepository.findAll();
    }

    /**
     * Gibt die Inspection mit der angegebenen ID zurück.
     *
     * @param id die eindeutige ID der gesuchten Inspection
     * @return ein Optional mit der gefundenen {@link Inspection}, oder leer wenn nicht existiert
     */
    @Transactional(readOnly = true)
    public Optional<Inspection> getInspectionById(Long id) {
        log.info("Fetching inspection with id {}", id);
        return inspectionRepository.findById(id);
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
     * @throws IllegalArgumentException wenn keine Checklist oder kein User mit den angegebenen
     * IDs existiert
     */
    public Inspection createInspectionFromChecklist(InspectionCreateRequest request) {
        log.info("Creating inspection for checklist {} at plant '{}'", request.getChecklistId(),
                request.getPlantName());
        
        Checklist checklist = checklistRepository.findById(request.getChecklistId()).orElseThrow(
                () -> new IllegalArgumentException("Checklist with id " + request.getChecklistId() + " not found"));

        // User laden (jetzt Pflichtfeld)
        User assignedInspector = userRepository.findById(request.getAssignedInspectorId()).orElseThrow(
                () -> new IllegalArgumentException("User with id " + request.getAssignedInspectorId() + " not found"));

        Inspection inspection = new Inspection();
        inspection.setChecklist(checklist);
        inspection.setTitle(request.getTitle() != null ? request.getTitle() : checklist.getName());
        inspection.setPlantName(request.getPlantName() != null ? request.getPlantName() : checklist.getPlantName());
        inspection.setPlannedDate(request.getPlannedDate());
        inspection.setStatus(InspectionStatus.PLANNED);
        inspection.setGeneralComment(request.getGeneralComment());
        inspection.setAssignedInspector(assignedInspector);

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

    /**
     * Liefert alle Inspektionen, die einem bestimmten Benutzer zugeordnet sind.
     *
     * @param userId ID des verantwortlichen Users
     * @return Liste der Inspektionen des Users (ggf. leer, aber niemals {@code null})
     */
    @Transactional(readOnly = true)
    public List<Inspection> getInspectionsForUser(Long userId) {
        log.info("Fetching inspections for user with id {}", userId);
        return inspectionRepository.findByAssignedInspectorId(userId);
    }

    /**
     * Aktualisiert den Status einer bestehenden Inspection.
     *
     * @param id die ID der zu aktualisierenden Inspection
     * @param newStatus der neue Statuswert als String
     * @return die aktualisierte {@link Inspection}
     * @throws IllegalArgumentException wenn keine Inspection mit der ID existiert oder
     * der Status ungültig ist
     */
    public Inspection updateStatus(Long id, String newStatus) {
        log.info("Updating status of inspection with id {} to {}", id, newStatus);

        Inspection inspection = getInspectionById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inspection with id " + id + " not found"));
        
        try {
            InspectionStatus status = InspectionStatus.valueOf(newStatus.toUpperCase());
            inspection.setStatus(status);
        } catch (IllegalArgumentException e) {
            log.error("Invalid status value: {}", newStatus);
            throw new IllegalArgumentException("Invalid status: " + newStatus + ". Allowed values: " + 
                java.util.Arrays.toString(InspectionStatus.values()));
        }

        Inspection saved = inspectionRepository.save(inspection);
        log.info("Updated status of inspection with id {} to {}", saved.getId(), newStatus);

        return saved;
    }

    /**
     * Löscht die Inspection mit der angegebenen ID.
     *
     * @param id die ID der zu löschenden Inspection
     * @throws IllegalArgumentException wenn keine Inspection mit der ID existiert
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