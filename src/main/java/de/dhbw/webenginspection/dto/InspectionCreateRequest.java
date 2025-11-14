package de.dhbw.webenginspection.dto;

import java.time.LocalDateTime;

public class InspectionCreateRequest {

    private Long checklistId;
    private String title;
    private String plantName;
    private LocalDateTime inspectionDate;
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
