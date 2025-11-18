package de.dhbw.webenginspection.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class InspectionCreateRequest {

    @NotNull(message = "checklistId is required")
    private Long checklistId;

    @NotBlank(message = "title is required")
    @Size(max = 255, message = "title must not exceed 255 characters")
    private String title;

    @NotBlank(message = "plantName is required")
    @Size(max = 255, message = "plantName must not exceed 255 characters")
    private String plantName;

    @NotNull(message = "inspectionDate is required")
    private LocalDateTime inspectionDate;

    @Size(max = 2000, message = "generalComment must not exceed 2000 characters")
    private String generalComment;

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
}
