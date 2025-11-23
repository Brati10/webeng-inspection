package de.dhbw.webenginspection.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

/**
 * JPA-Entität, die einen einzelnen Prüfschritt innerhalb einer
 * {@link Checklist} repräsentiert. Jeder Schritt enthält eine Beschreibung,
 * eine detaillierte Anforderung sowie eine Reihenfolge innerhalb der
 * Checkliste.
 */
@Entity
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class ChecklistStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Beschreibung des Prüfschritts, wie er in der UI dargestellt wird.
     * Beispiel: "Überprüfung des Ölstands".
     */
    @Column(nullable = false, length = 1000)
    private String description;

    /**
     * Optionale genauere Anforderung zu diesem Prüfschritt. Beispiel: "Ölstand
     * muss zwischen Min und Max liegen".
     */
    @Column(length = 1000)
    private String requirement;

    /**
     * Reihenfolge des Prüfschritts innerhalb der Checkliste. Niedrige Werte
     * bedeuten frühere Positionen.
     */
    private Integer orderIndex;

    /**
     * Die Checkliste, zu der dieser Schritt gehört. Wird in JSON ausgeblendet,
     * um zyklische Referenzen zu verhindern.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_id")
    @JsonIgnore // verhindert Endlos-Schleifen bei JSON (Checklist -> Steps ->
                // Checklist -> ...)
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
