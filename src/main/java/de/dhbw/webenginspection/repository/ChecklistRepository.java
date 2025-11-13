package de.dhbw.webenginspection.repository;

import de.dhbw.webenginspection.entity.Checklist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChecklistRepository extends JpaRepository<Checklist, Long> {

    // Alle Checklisten für eine bestimmte Anlage
    List<Checklist> findByPlantName(String plantName);

    // Volltextsuche im Namen (z.B. für Filter im UI)
    List<Checklist> findByNameContainingIgnoreCase(String namePart);
}
