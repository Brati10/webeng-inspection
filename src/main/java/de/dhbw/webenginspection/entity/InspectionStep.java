package de.dhbw.webenginspection.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

/**
 * JPA-Entität, die einen einzelnen konkreten Schritt innerhalb einer
 * {@link Inspection} repräsentiert. Jeder InspectionStep ist einem
 * {@link ChecklistStep} zugeordnet und enthält den tatsächlichen Status,
 * Kommentare sowie optionale Foto-Informationen zur durchgeführten Prüfung.
 */
@Entity
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class InspectionStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Ergebnis bzw. Status des Prüfschritts, z.&nbsp;B. PASSED, FAILED oder
     * NOT_APPLICABLE. Wird als String gespeichert, da {@link StepStatus} als
     * Enum betrieben wird.
     */
    @Enumerated(EnumType.STRING)
    private StepStatus status;

    /**
     * Optionaler Kommentar zum Prüfschritt, z.&nbsp;B. Notizen zur Bewertung
     * oder Beobachtungen während der Durchführung.
     */
    @Column(length = 2000)
    private String comment;

    /**
     * Pfad oder URL zu einem optionalen Beweisfoto für diesen Schritt. Kann
     * später zu einem Blob, Remote-Storage-Link oder Base64 ersetzt werden.
     */
    private String photoPath;

    /**
     * Die Inspection, zu der dieser konkrete Schritt gehört.
     * 
     * @JsonIgnore verhindert zyklische Serialisierung (Inspection -> Steps ->
     * Inspection ...).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspection_id")
    @JsonIgnore
    private Inspection inspection;

    /**
     * Der Template-Schritt aus der Checkliste, auf dessen Grundlage dieser
     * InspectionStep erstellt wurde. Enthält die ursprüngliche Beschreibung und
     * Reihenfolge.
     */
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
