package de.dhbw.webenginspection.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class InspectionStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private StepStatus status;

    @Column(length = 2000)
    private String comment;

    // Pfad zu einem Foto (oder später: URL, Blob, etc.)
    private String photoPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspection_id")
    @JsonIgnore
    private Inspection inspection;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "checklist_step_id")
    private ChecklistStep checklistStep;

    // --- Konstruktoren ---

    public InspectionStep() {
        
    }

    public InspectionStep(StepStatus status, String comment, String photoPath) {
        this.status = status;
        this.comment = comment;
        this.photoPath = photoPath;
    }

    // --- Getter & Setter ---

    public Long getId() {
        return id;
    }

    public StepStatus getStatus() {
        return status;
    }

    public String getComment() {
        return comment;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public Inspection getInspection() {
        return inspection;
    }

    public ChecklistStep getChecklistStep() {
        return checklistStep;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setStatus(StepStatus status) {
        this.status = status;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public void setInspection(Inspection inspection) {
        this.inspection = inspection;
    }

    public void setChecklistStep(ChecklistStep checklistStep) {
        this.checklistStep = checklistStep;
    }
}
