package de.dhbw.webenginspection.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

import de.dhbw.webenginspection.controller.GlobalExceptionHandler;
import de.dhbw.webenginspection.entity.Checklist;

/**
 * Data Transfer Object zum Erstellen einer neuen {@link Inspection}. Enthält
 * alle notwendigen Felder, die vom Client (z. B. dem React-Frontend)
 * bereitgestellt werden müssen, einschließlich der Referenz zur zugehörigen
 * {@link Checklist}.
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
     * Zeitpunkt der geplanten Inspection. Wird als {@link LocalDateTime}
     * erwartet; muss ein gültiges Datum/Zeit-Format haben.
     */
    @NotNull(message = "inspectionDate is required")
    private LocalDateTime inspectionDate;

    /**
     * Optionaler allgemeiner Kommentar zur Inspection. Wird z. B. für
     * Kontextinformationen oder Vorbemerkungen genutzt.
     */
    @Size(max = 2000, message = "generalComment must not exceed 2000 characters")
    private String generalComment;

    /**
     * ID des verantwortlichen Users, der die Inspection durchführen soll.
     */
    // KEIN @NotNull, damit Tests erstmal weiter funktionieren; später evtl.
    // verpflichtend
    private Long responsibleUserId;

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

    public LocalDateTime getInspectionDate() {
        return inspectionDate;
    }

    public void setInspectionDate(LocalDateTime inspectionDate) {
        this.inspectionDate = inspectionDate;
    }

    public String getGeneralComment() {
        return generalComment;
    }

    public void setGeneralComment(String generalComment) {
        this.generalComment = generalComment;
    }

    public Long getResponsibleUserId() {
        return responsibleUserId;
    }

    public void setResponsibleUserId(Long responsibleUserId) {
        this.responsibleUserId = responsibleUserId;
    }
}
