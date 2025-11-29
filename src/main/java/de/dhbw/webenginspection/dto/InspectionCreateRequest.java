package de.dhbw.webenginspection.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

import de.dhbw.webenginspection.controller.GlobalExceptionHandler;

/**
 * Data Transfer Object zum Erstellen einer neuen {@link Inspection}. Enthält
 * alle notwendigen Felder, die vom Client (z. B. dem React-Frontend)
 * bereitgestellt werden müssen.
 *
 * Die Validierungsannotationen stellen sicher, dass bei fehlerhaften Eingaben
 * der {@link GlobalExceptionHandler} automatisch passende Fehlermeldungen
 * zurückliefert.
 */
public class InspectionCreateRequest {

    /**
     * Die ID der Checklist, auf deren Basis die Inspection erzeugt wird. Muss
     * zwingend gesetzt sein.
     */
    @NotNull(message = "checklistId is required")
    private Long checklistId;

    /**
     * Titel der Inspection, wie er im Frontend angezeigt wird. Beispiel:
     * "Inspektion Turbine A - Januar".
     */
    @NotBlank(message = "title is required")
    @Size(max = 255, message = "title must not exceed 255 characters")
    private String title;

    /**
     * Name bzw. Standort der Anlage, an der die Inspection durchgeführt wird.
     * Beispiel: "Werk Mannheim".
     */
    @NotBlank(message = "plantName is required")
    @Size(max = 255, message = "plantName must not exceed 255 characters")
    private String plantName;

    /**
     * Geplanter Zeitpunkt der Inspection.
     */
    @NotNull(message = "plannedDate is required")
    private LocalDateTime plannedDate;

    /**
     * Optionaler allgemeiner Kommentar zur Inspection. Wird z. B. für
     * Kontextinformationen oder Vorbemerkungen genutzt.
     */
    @Size(max = 2000, message = "generalComment must not exceed 2000 characters")
    private String generalComment;

    /**
     * ID des verantwortlichen Inspektors, der die Inspection durchführen soll.
     */
    @NotNull(message = "assignedInspectorId is required")
    private Long assignedInspectorId;

    public InspectionCreateRequest() {
    }

    public Long getChecklistId() {
        return checklistId;
    }

    public void setChecklistId(Long checklistId) {
        this.checklistId = checklistId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public LocalDateTime getPlannedDate() {
        return plannedDate;
    }

    public void setPlannedDate(LocalDateTime plannedDate) {
        this.plannedDate = plannedDate;
    }

    public String getGeneralComment() {
        return generalComment;
    }

    public void setGeneralComment(String generalComment) {
        this.generalComment = generalComment;
    }

    public Long getAssignedInspectorId() {
        return assignedInspectorId;
    }

    public void setAssignedInspectorId(Long assignedInspectorId) {
        this.assignedInspectorId = assignedInspectorId;
    }
}