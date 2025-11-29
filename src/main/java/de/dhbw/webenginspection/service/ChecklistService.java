package de.dhbw.webenginspection.service;

import de.dhbw.webenginspection.entity.Checklist;
import de.dhbw.webenginspection.entity.ChecklistStep;
import de.dhbw.webenginspection.repository.ChecklistRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Service zur Verwaltung von Checklisten. Bietet Funktionen zum Erstellen,
 * Lesen, Aktualisieren und Löschen von {@link Checklist}-Entitäten und stellt
 * sicher, dass die bidirektionalen Beziehungen zwischen Checklist und
 * ChecklistStep konsistent gesetzt werden.
 */
@Service
@Transactional
public class ChecklistService {

    private static final Logger log = LoggerFactory.getLogger(ChecklistService.class);

    private final ChecklistRepository checklistRepository;

    public ChecklistService(ChecklistRepository checklistRepository) {
        this.checklistRepository = checklistRepository;
    }

    /**
     * Gibt alle vorhandenen Checklisten zurück.
     *
     * @return eine Liste aller {@link Checklist}-Entitäten (niemals {@code null})
     */
    public List<Checklist> getAllChecklists() {
        log.info("Fetching all checklists");
        return checklistRepository.findAll();
    }

    /**
     * Gibt die Checklist mit der angegebenen ID zurück.
     *
     * @param id die eindeutige ID der gesuchten Checklist
     * @return ein Optional mit der gefundenen {@link Checklist}, oder leer wenn nicht existiert
     */
    @Transactional(readOnly = true)
    public Optional<Checklist> getChecklistById(Long id) {
        log.info("Fetching checklist with id {}", id);
        return checklistRepository.findById(id);
    }

    /**
     * Erstellt eine neue Checklist und speichert sie in der Datenbank. Setzt
     * außerdem die bidirektionale Beziehung zu den enthaltenen ChecklistSteps
     * korrekt, indem jedem Step die entsprechende Checklist zugewiesen wird.
     *
     * @param checklist die zu erstellende {@link Checklist} inklusive
     * optionaler Schritte
     * @return die gespeicherte Checklist mit generierter ID
     */
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

    /**
     * Aktualisiert die vorhandene Checklist mit der angegebenen ID. Dabei
     * werden die Eigenschaften name, plantName und recommendations
     * überschrieben und die gesamte Liste der zugehörigen Steps ersetzt. Jedem
     * übergebenen Step wird die bestehende Checklist zugewiesen.
     *
     * @param id die ID der zu aktualisierenden Checklist
     * @param updated die neuen Daten für die Checklist
     * @return die aktualisierte und gespeicherte {@link Checklist}
     * @throws IllegalArgumentException wenn keine Checklist mit der ID
     * existiert
     */
    public Checklist updateChecklist(Long id, Checklist updated) {
        log.info("Updating checklist with id {}", id);
        
        Checklist existing = getChecklistById(id)
                .orElseThrow(() -> new IllegalArgumentException("Checklist with id " + id + " not found"));

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

    /**
     * Löscht die Checklist mit der angegebenen ID.
     *
     * @param id die ID der zu löschenden Checklist
     * @throws IllegalArgumentException wenn keine Checklist mit der ID
     * existiert
     */
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