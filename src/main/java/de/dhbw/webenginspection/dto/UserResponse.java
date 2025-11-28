package de.dhbw.webenginspection.dto;

import de.dhbw.webenginspection.entity.User;
import de.dhbw.webenginspection.entity.UserRole;

/**
 * Response-DTO mit den wichtigsten User-Infos, die ans Frontend zurückgegeben
 * werden dürfen.
 */
public class UserResponse {

    private Long id;

    private String username;

    private String displayName;

    private UserRole role;

    public UserResponse() {
        // für Jackson
    }

    public UserResponse(Long id, String username, String displayName, UserRole role) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.role = role;
    }

    public static UserResponse fromEntity(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getDisplayName(), user.getRole());
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public UserRole getRole() {
        return role;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
