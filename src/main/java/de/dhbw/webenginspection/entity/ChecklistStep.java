package de.dhbw.webenginspection.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class ChecklistStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Beschreibung des Prüfschritts
    @Column(nullable = false, length = 1000)
    private String description;

    // Konkrete Anforderung
    @Column(length = 1000)
    private String requirement;

    // Reihenfolge in der Checkliste
    private Integer orderIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_id")
    @JsonIgnore // verhindert Endlos-Schleifen bei JSON (Checklist -> Steps -> Checklist -> ...)
    private Checklist checklist;

    // --- Konstruktoren ---

    public ChecklistStep() {

    }

    public ChecklistStep(String description, String requirement, Integer orderIndex) {
        this.description = description;
        this.requirement = requirement;
        this.orderIndex = orderIndex;
    }

    // --- Getter & Setter ---

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getRequirement() {
        return requirement;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public Checklist getChecklist() {
        return checklist;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public void setChecklist(Checklist checklist) {
        this.checklist = checklist;
    }
}
