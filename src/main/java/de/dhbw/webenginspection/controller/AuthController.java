package de.dhbw.webenginspection.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import de.dhbw.webenginspection.dto.LoginRequest;
import de.dhbw.webenginspection.dto.UserResponse;
import de.dhbw.webenginspection.entity.User;
import de.dhbw.webenginspection.service.UserService;

/**
 * REST-Controller für Authentifizierung und User-bezogene Endpunkte.
 *
 * Hinweis: Aktuell wird hier noch kein echtes Session-/Token-Handling gemacht.
 * Der Login-Endpunkt prüft Benutzername/Passwort und liefert bei Erfolg die
 * User-Daten zurück. Das Frontend kann darauf aufbauen.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Prüft Benutzername und Passwort. Bei Erfolg werden die User-Daten (ohne
     * Passwort) zurückgegeben. Bei Fehlern wird eine 400 / 401-artige Antwort
     * durch den GlobalExceptionHandler erzeugt.
     */
    @PostMapping("/login")
    public UserResponse login(@Validated
    @RequestBody
    LoginRequest request) {
        log.info("Login attempt for user '{}'", request.getUsername());
        User user = userService.validateLogin(request.getUsername(), request.getPassword());
        log.info("Login successful for user '{}'", user.getUsername());
        return UserResponse.fromEntity(user);
    }

    /**
     * Liefert die User-Informationen zu einer gegebenen User-ID. Dieser
     * Endpoint ist als einfache Variante für das Frontend gedacht, um z. B.
     * nach einem Login den User erneut zu laden.
     *
     * Später könnte hier der aktuell authentifizierte User (aus
     * SecurityContext) zurückgegeben werden.
     */
    @GetMapping("/me")
    public UserResponse getMe(@RequestParam("userId")
    Long userId) {
        User user = userService.getById(userId);
        return UserResponse.fromEntity(user);
    }
}
