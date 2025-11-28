package de.dhbw.webenginspection.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA-Entität, die einen Benutzer der Anwendung repräsentiert. Ein User kann
 * für mehrere Inspektionen als verantwortlicher Mitarbeiter eingetragen sein.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Eindeutiger Login-Name.
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * Angezeigter Name in der Oberfläche (z. B. "Max Mustermann").
     */
    @Column(nullable = false)
    private String displayName;

    /**
     * BCrypt-gehashes Passwort.
     */
    @Column(nullable = false)
    private String passwordHash;

    /**
     * Rolle des Users (z. B. INSPECTOR, ADMIN).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.INSPECTOR;

    /**
     * Inspektionen, für die dieser Benutzer verantwortlich ist.
     */
    @OneToMany(mappedBy = "assignedInspector")
    private List<Inspection> inspections = new ArrayList<>();

    // ---------------------------------------------------------------------------------
    // Konstruktoren
    // ---------------------------------------------------------------------------------

    protected User() {
        // Für JPA
    }

    public User(String username, String displayName, String passwordHash, UserRole role) {
        this.username = username;
        this.displayName = displayName;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    // ---------------------------------------------------------------------------------
    // Getter / Setter
    // ---------------------------------------------------------------------------------

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public List<Inspection> getInspections() {
        return inspections;
    }

    public void setInspections(List<Inspection> inspections) {
        this.inspections = inspections;
    }

    // Convenience-Methode, wenn du später Inspektionen zuordnen willst
    public void addInspection(Inspection inspection) {
        this.inspections.add(inspection);
        inspection.setAssignedInspector(this);
    }
}
