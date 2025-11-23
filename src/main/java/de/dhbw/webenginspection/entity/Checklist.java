package de.dhbw.webenginspection.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA-Entität, die eine Checkliste repräsentiert. Enthält grundlegende
 * Metadaten (Name, Anlage, Empfehlungen) sowie eine Liste zugehöriger
 * {@link ChecklistStep}-Entitäten. Stellt außerdem Hilfsmethoden zur Verwaltung
 * der bidirektionalen Beziehung bereit.
 */
@Entity
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Checklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // Name der Anlage
    private String plantName;

    /**
     * Optionale allgemeine Empfehlungen oder Hinweise zur Checkliste. Wird
     * typischerweise im Vorfeld einer Inspection verwendet.
     */
    @Column(length = 2000)
    private String recommendations;

    /**
     * Liste der zugehörigen Schritte dieser Checkliste. Die Beziehung ist
     * bidirektional und wird über das Feld {@code checklist} in
     * {@link ChecklistStep} abgebildet. Änderungen werden automatisch
     * persistiert (CascadeType.ALL).
     */
    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChecklistStep> steps = new ArrayList<>();

    // --- Konstruktoren ---

    public Checklist() {

    }

    public Checklist(String name, String plantName, String recommendations) {
        this.name = name;
        this.plantName = plantName;
        this.recommendations = recommendations;
    }

    // --- Methoden für Bidirektionalität ---

    /**
     * Fügt der Checkliste einen neuen Schritt hinzu und setzt die
     * bidirektionale Beziehung korrekt.
     *
     * @param step der hinzuzufügende {@link ChecklistStep}
     */
    public void addStep(ChecklistStep step) {
        steps.add(step);
        step.setChecklist(this);
    }

    /**
     * Entfernt einen Schritt aus der Checkliste und löst die bidirektionale
     * Beziehung.
     *
     * @param step der zu entfernende {@link ChecklistStep}
     */
    public void removeStep(ChecklistStep step) {
        steps.remove(step);
        step.setChecklist(null);
    }

    // --- Getter & Setter ---

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPlantName() {
        return plantName;
    }

    public String getRecommendations() {
        return recommendations;
    }

    public List<ChecklistStep> getSteps() {
        return steps;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
    }

    public void setSteps(List<ChecklistStep> steps) {
        this.steps = steps;
    }
}
