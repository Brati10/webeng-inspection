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
import java.util.Optional;

/**
 * Service zur Verwaltung von {@link InspectionStep}-Entitäten. Bietet
 * Funktionen zum Lesen, Erstellen, Aktualisieren und Löschen einzelner
 * Inspektionsschritte sowie zum Filtern nach Status. Stellt sicher, dass jeder
 * Step korrekt mit der zugehörigen {@link Inspection} und dem zugrunde
 * liegenden {@link ChecklistStep} verknüpft wird.
 */
@Service
@Transactional
public class InspectionStepService {

    private static final Logger log = LoggerFactory.getLogger(InspectionStepService.class);

    private final InspectionStepRepository inspectionStepRepository;

    private final InspectionRepository inspectionRepository;

    private final ChecklistStepRepository checklistStepRepository;

    public InspectionStepService(InspectionStepRepository inspectionStepRepository,
            InspectionRepository inspectionRepository, ChecklistStepRepository checklistStepRepository) {
        this.inspectionStepRepository = inspectionStepRepository;
        this.inspectionRepository = inspectionRepository;
        this.checklistStepRepository = checklistStepRepository;
    }

    /**
     * Gibt alle Schritte einer Inspektion zurück.
     *
     * @param inspectionId die ID der Inspektion
     * @return eine Liste der zugehörigen {@link InspectionStep}-Entitäten
     */
    public List<InspectionStep> getStepsForInspection(Long inspectionId) {
        return inspectionStepRepository.findByInspectionId(inspectionId);
    }

    /**
     * Gibt alle Schritte einer Inspektion zurück, gefiltert nach einem
     * bestimmten Status.
     *
     * @param inspectionId die ID der Inspektion
     * @param status der gewünschte {@link StepStatus}
     * @return alle Schritte der Inspektion, die den angegebenen Status haben
     */
    public List<InspectionStep> getStepsForInspectionByStatus(Long inspectionId, StepStatus status) {
        return inspectionStepRepository.findByInspectionIdAndStatus(inspectionId, status);
    }

    /**
     * Ruft einen einzelnen InspectionStep anhand seiner ID ab.
     *
     * @param id die ID des gesuchten Schritts
     * @return ein Optional mit dem gefundenen {@link InspectionStep}, oder leer wenn nicht existiert
     */
    public Optional<InspectionStep> getStepById(Long id) {
        return inspectionStepRepository.findById(id);
    }

    /**
     * Erstellt einen neuen Schritt für eine bestehende Inspektion. Verknüpft
     * den Schritt sowohl mit der Inspection als auch mit dem zugrunde liegenden
     * ChecklistStep. Falls kein Status gesetzt wurde, wird standardmäßig
     * {@link StepStatus#NOT_APPLICABLE} verwendet.
     *
     * @param inspectionId die ID der Inspektion, zu der der neue Schritt gehört
     * @param checklistStepId die ID des zugehörigen ChecklistSteps
     * @param stepData die zu erstellende {@link InspectionStep}-Entität
     * @return der gespeicherte Schritt mit generierter ID
     * @throws IllegalArgumentException wenn die Inspection oder der
     * ChecklistStep nicht existieren
     */
    public InspectionStep createStep(Long inspectionId, Long checklistStepId, InspectionStep stepData) {
        log.info("Creating new step for inspection with id {}", inspectionId);

        Inspection inspection = inspectionRepository.findById(inspectionId)
                .orElseThrow(() -> new IllegalArgumentException("Inspection with id " + inspectionId + " not found"));

        ChecklistStep checklistStep = checklistStepRepository.findById(checklistStepId).orElseThrow(
                () -> new IllegalArgumentException("ChecklistStep with id " + checklistStepId + " not found"));

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
     * Aktualisiert einen bestehenden Inspektionsschritt vollständig. Verändert
     * Status, Kommentar und photoPath, lässt jedoch die Verknüpfungen zu
     * Inspection und ChecklistStep unverändert.
     *
     * @param id die ID des zu aktualisierenden Schritts
     * @param updated ein Objekt mit den neuen Werten
     * @return der aktualisierte und gespeicherte {@link InspectionStep}
     * @throws IllegalArgumentException wenn kein Schritt mit der ID existiert
     */
    public InspectionStep updateStep(Long id, InspectionStep updated) {
        log.info("Updating inspection step with id {}", id);

        InspectionStep existing = getStepById(id)
                .orElseThrow(() -> new IllegalArgumentException("InspectionStep with id " + id + " not found"));

        existing.setStatus(updated.getStatus());
        existing.setComment(updated.getComment());
        existing.setPhotoPath(updated.getPhotoPath());

        InspectionStep saved = inspectionStepRepository.save(existing);
        log.info("Updated inspection step with id {}", saved.getId());

        return saved;
    }

    /**
     * Aktualisiert ausschließlich den Status eines bestehenden
     * Inspektionsschritts.
     *
     * @param id die ID des zu aktualisierenden Schritts
     * @param newStatus der neue {@link StepStatus}
     * @return der gespeicherte {@link InspectionStep} mit aktualisiertem Status
     * @throws IllegalArgumentException wenn kein Schritt mit der ID existiert
     */
    public InspectionStep updateStatus(Long id, StepStatus newStatus) {
        log.info("Updating status of inspection step with id {}", id);

        InspectionStep existing = getStepById(id)
                .orElseThrow(() -> new IllegalArgumentException("InspectionStep with id " + id + " not found"));
        existing.setStatus(newStatus);

        InspectionStep saved = inspectionStepRepository.save(existing);
        log.info("Updated status of inspection step with id {}", saved.getId());

        return saved;
    }

    /**
     * Aktualisiert ausschließlich den Kommentar eines bestehenden
     * Inspektionsschritts.
     *
     * @param id die ID des zu aktualisierenden Schritts
     * @param newComment der neue Kommentartext
     * @return der aktualisierte {@link InspectionStep}
     * @throws IllegalArgumentException wenn kein Schritt mit der ID existiert
     */
    public InspectionStep updateComment(Long id, String newComment) {
        log.info("Updating comment of inspection step with id {}", id);

        InspectionStep existing = getStepById(id)
                .orElseThrow(() -> new IllegalArgumentException("InspectionStep with id " + id + " not found"));
        existing.setComment(newComment);

        InspectionStep saved = inspectionStepRepository.save(existing);
        log.info("Updated comment of inspection step with id {}", saved.getId());

        return saved;
    }

    /**
     * Löscht einen bestehenden Inspektionsschritt anhand seiner ID.
     *
     * @param id die ID des zu löschenden Schritts
     * @throws IllegalArgumentException wenn kein Schritt mit der ID existiert
     */
    public void deleteStep(Long id) {
        log.info("Deleting inspection step with id {}", id);

        if (!inspectionStepRepository.existsById(id)) {
            log.warn("InspectionStep with id {} not found for deletion", id);
            throw new IllegalArgumentException("InspectionStep with id " + id + " not found");
        }

        inspectionStepRepository.deleteById(id);
        log.info("Deleted inspection step with id {}", id);
    }
}
