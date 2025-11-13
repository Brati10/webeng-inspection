package de.dhbw.webenginspection.repository;

import de.dhbw.webenginspection.entity.ChecklistStep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChecklistStepRepository extends JpaRepository<ChecklistStep, Long> {
    
    // Schritte zu einer Checkliste, sortiert nach orderIndex
    List<ChecklistStep> findByChecklistIdOrderByOrderIndex(Long checklistId);
}
