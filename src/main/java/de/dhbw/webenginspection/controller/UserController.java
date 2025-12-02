package de.dhbw.webenginspection.controller;

import de.dhbw.webenginspection.dto.UserResponse;
import de.dhbw.webenginspection.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * REST-Controller für die Verwaltung von Benutzern.
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Gibt alle Benutzer zurück (nur für Admins).
     *
     * @return Liste aller Benutzer
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAll() {
        log.info("Fetching all users");
        return userService.getAllUsers().stream().map(UserResponse::fromEntity).toList();
    }

    /**
     * Gibt einen einzelnen Benutzer anhand seiner ID zurück (nur für Admins).
     *
     * @param id die ID des gesuchten Benutzers
     * @return {@code 200 OK} mit dem Benutzer oder {@code 404 Not Found}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getById(@PathVariable
    Long id) {
        log.info("Fetching user with id {}", id);
        return userService.getUserById(id).map(user -> ResponseEntity.ok(UserResponse.fromEntity(user)))
                .orElse(ResponseEntity.notFound().build());
    }
}