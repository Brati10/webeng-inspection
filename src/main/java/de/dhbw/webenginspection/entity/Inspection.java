package de.dhbw.webenginspection.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Inspection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Titel der Inspektion
    private String title;

    // Name der Anlage (kann von Checklist kommen oder abweichen)
    private String plantName;

    private LocalDateTime inspectionDate;

    // Status als String (z. B. "PLANNED", "IN_PROGRESS", "COMPLETED")
    private String status;

    @Column(length = 2000)
    private String generalComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_id")
    private Checklist checklist;

    @OneToMany(
            mappedBy = "inspection",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<InspectionStep> steps = new ArrayList<>();

    // --- Konstruktoren ---

    public Inspection() {

    }

    public Inspection(String title, String plantName, LocalDateTime inspectionDate, String status) {
        this.title = title;
        this.plantName = plantName;
        this.inspectionDate = inspectionDate;
        this.status = status;
    }

    // Methoden für Bidirektionalität

    public void addStep(InspectionStep step) {
        steps.add(step);
        step.setInspection(this);
    }

    public void removeStep(InspectionStep step) {
        steps.remove(step);
        step.setInspection(null);
    }

    // --- Getter & Setter ---

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPlantName() {
        return plantName;
    }

    public LocalDateTime getInspectionDate() {
        return inspectionDate;
    }

    public String getStatus() {
        return status;
    }

    public String getGeneralComment() {
        return generalComment;
    }

    public Checklist getChecklist() {
        return checklist;
    }

    public List<InspectionStep> getSteps() {
        return steps;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public void setInspectionDate(LocalDateTime inspectionDate) {
        this.inspectionDate = inspectionDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setGeneralComment(String generalComment) {
        this.generalComment = generalComment;
    }

    public void setChecklist(Checklist checklist) {
        this.checklist = checklist;
    }

    public void setSteps(List<InspectionStep> steps) {
        this.steps = steps;
    }
}
