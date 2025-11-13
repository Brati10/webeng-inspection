package de.dhbw.webenginspection.repository;

import de.dhbw.webenginspection.entity.InspectionStep;
import de.dhbw.webenginspection.entity.StepStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InspectionStepRepository extends JpaRepository<InspectionStep, Long> {
    
    // Alle Schritte einer Inspektion (z. B. für Detailansicht)
    List<InspectionStep> findByInspectionId(Long inspectionId);

    // Alle Schritte einer Inspektion mit bestimmtem Status (z. B. alle FAILED)
    List<InspectionStep> findByInspectionIdAndStatus(Long inspectionId, StepStatus status);
}
