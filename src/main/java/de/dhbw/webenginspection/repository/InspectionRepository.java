package de.dhbw.webenginspection.repository;

import de.dhbw.webenginspection.entity.Inspection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface InspectionRepository extends JpaRepository<Inspection, Long> {

    // Alle Inspektionen mit einem bestimmten Status (z. B. PLANNED,
    // IN_PROGRESS, COMPLETED)
    List<Inspection> findByStatus(String status);

    // Inspektionen für eine bestimmte Anlage
    List<Inspection> findByPlantName(String plantName);

    // Geplante / durchgeführte Inspektionen in einem Zeitraum
    List<Inspection> findByPlannedDateBetween(LocalDateTime from, LocalDateTime to);

    // Inspektionen, die einem bestimmten User (verantwortlicher Mitarbeiter)
    // zugeordnet sind
    List<Inspection> findByAssignedInspectorId(Long userId);

    // Anzahl der Inspektionen, die eine bestimmte Checkliste verwenden
    long countByChecklistId(Long checklistId);
}
