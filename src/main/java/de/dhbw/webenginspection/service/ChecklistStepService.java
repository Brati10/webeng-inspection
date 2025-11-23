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

/**
 * Service zur Verwaltung von {@link ChecklistStep}-Entitäten. Beinhaltet
 * Funktionen zum Lesen, Erstellen, Aktualisieren und Löschen von Schritten
 * innerhalb einer Checkliste. Stellt sicher, dass jeder Step korrekt mit seiner
 * zugehörigen {@link Checklist} verknüpft wird.
 */
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
     * Gibt alle Schritte einer Checkliste zurück, sortiert nach dem Feld
     * {@code orderIndex}.
     *
     * @param checklistId die ID der Checkliste, deren Schritte abgefragt werden
     * sollen
     * @return eine sortierte Liste der zugehörigen
     * {@link ChecklistStep}-Entitäten
     */
    public List<ChecklistStep> getStepsForChecklist(Long checklistId) {
        return checklistStepRepository.findByChecklistIdOrderByOrderIndex(checklistId);
    }

    /**
     * Gibt einen einzelnen ChecklistStep anhand seiner ID zurück.
     *
     * @param id die ID des gesuchten Schritt-Objekts
     * @return der gefundene {@link ChecklistStep}
     * @throws IllegalArgumentException wenn kein Schritt mit der angegebenen ID
     * existiert
     */
    public ChecklistStep getStepById(Long id) {
        return checklistStepRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ChecklistStep with id " + id + " not found"));
    }

    /**
     * Erstellt einen neuen Schritt für eine bestehende Checkliste. Setzt die
     * bidirektionale Beziehung korrekt, indem dem Step die passende Checklist
     * zugewiesen wird.
     *
     * @param checklistId die ID der Checkliste, zu der der neue Schritt gehört
     * @param step die zu erstellende {@link ChecklistStep}-Entität
     * @return der gespeicherte Schritt mit generierter ID
     * @throws IllegalArgumentException wenn keine Checkliste mit der
     * angegebenen ID existiert
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
     * Aktualisiert einen bestehenden Schritt einer Checkliste. Aktualisiert
     * Beschreibung, Anforderung und Reihenfolge-Index.
     *
     * @param id die ID des zu aktualisierenden Schritts
     * @param updated ein Objekt mit den neuen Werten für den Schritt
     * @return der aktualisierte und gespeicherte {@link ChecklistStep}
     * @throws IllegalArgumentException wenn kein Schritt mit der angegebenen ID
     * existiert
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
     * Löscht einen bestehenden Schritt anhand seiner ID.
     *
     * @param id die ID des zu löschenden Schritts
     * @throws IllegalArgumentException wenn kein Schritt mit der angegebenen ID
     * existiert
     */
    public void deleteStep(Long id) {
        log.info("Deleting checklist step with id {}", id);

        if (!checklistStepRepository.existsById(id)) {
            log.warn("ChecklistStep with id {} not found for deletion", id);
            throw new IllegalArgumentException("ChecklistStep with id " + id + " not found");
        }

        checklistStepRepository.deleteById(id);
        log.info("Deleted checklist step with id {}", id);
    }
}
