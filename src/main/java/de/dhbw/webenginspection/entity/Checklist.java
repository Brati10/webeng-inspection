package de.dhbw.webenginspection.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // kann man noch sauberer über DTOs lösen, evtl. ToDo für später
@Entity
public class Checklist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // Name der Anlage
    private String plantName;

    // Allgemeine Empfehlungen / Hinweise
    @Column(length = 2000)
    private String recommendations;

    @OneToMany(
        mappedBy = "checklist",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
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

    public void addStep(ChecklistStep step) {
        steps.add(step);
        step.setChecklist(this);
    }

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
