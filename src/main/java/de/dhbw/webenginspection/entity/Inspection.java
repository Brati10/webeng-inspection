package de.dhbw.webenginspection.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA-Entität, die eine konkrete Durchführung einer Inspection repräsentiert.
 * Verknüpft eine {@link Checklist} mit einem Zeitpunkt, einem Status und den
 * dazugehörigen {@link InspectionStep}-Instanzen, in denen die Ergebnisse der
 * einzelnen Schritte festgehalten werden.
 */
@Entity
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
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

    /**
     * Optionaler allgemeiner Kommentar zur Inspection, z.&nbsp;B. für
     * übergreifende Beobachtungen oder Hinweise.
     */
    @Column(length = 2000)
    private String generalComment;

    /**
     * Die zugrunde liegende {@link Checklist}, auf deren Basis diese Inspection
     * durchgeführt wird.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_id")
    private Checklist checklist;

    /**
     * Alle konkreten Schritte dieser Inspection. Die Beziehung ist
     * bidirektional und wird über das Feld {@code inspection} in
     * {@link InspectionStep} abgebildet.
     */
    @OneToMany(mappedBy = "inspection", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InspectionStep> steps = new ArrayList<>();

    /**
     * Verantwortlicher Mitarbeiter, der diese Inspection durchführt.
     */
    @ManyToOne
    @JoinColumn(name = "assigned_inspector_id")
    private User assignedInspector;

    // --- Konstruktoren ---

    public Inspection() {

    }

    public Inspection(String title, String plantName, LocalDateTime inspectionDate, String status) {
        this.title = title;
        this.plantName = plantName;
        this.inspectionDate = inspectionDate;
        this.status = status;
    }

    // --- Methoden für Bidirektionalität ---

    /**
     * Fügt der Inspection einen neuen Schritt hinzu und setzt die
     * bidirektionale Beziehung korrekt.
     *
     * @param step der hinzuzufügende {@link InspectionStep}
     */
    public void addStep(InspectionStep step) {
        steps.add(step);
        step.setInspection(this);
    }

    /**
     * Entfernt einen Schritt aus der Inspection und löst die bidirektionale
     * Beziehung.
     *
     * @param step der zu entfernende {@link InspectionStep}
     */
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

    public User getAssignedInspector() {
        return assignedInspector;
    }

    public void setAssignedInspector(User assignedInspector) {
        this.assignedInspector = assignedInspector;
    }
}
