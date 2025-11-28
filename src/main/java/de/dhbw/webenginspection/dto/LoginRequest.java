package de.dhbw.webenginspection.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request-DTO für den Login-Endpunkt.
 */
public class LoginRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    public LoginRequest() {
        // für Jackson
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
