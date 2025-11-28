package de.dhbw.webenginspection.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.dhbw.webenginspection.entity.User;
import de.dhbw.webenginspection.entity.UserRole;
import de.dhbw.webenginspection.repository.UserRepository;

/**
 * Service zur Verwaltung von Benutzern und zum Umgang mit Passwörtern.
 */
@Service
@Transactional
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Legt einen neuen Benutzer mit gehashtem Passwort an.
     */
    public User createUser(String username, String displayName, String rawPassword, UserRole role) {
        log.info("Creating new user with username {}", username);

        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username '" + username + "' is already taken");
        }

        String passwordHash = passwordEncoder.encode(rawPassword);

        User user = new User(username, displayName, passwordHash, role);
        return userRepository.save(user);
    }

    /**
     * Liefert alle Benutzer zurück.
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Sucht einen Benutzer anhand des Usernamens.
     */
    @Transactional(readOnly = true)
    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User with username " + username + " not found"));
    }

    /**
     * Sucht einen Benutzer anhand der ID.
     */
    @Transactional(readOnly = true)
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + id + " not found"));
    }

    /**
     * Hilfsmethode, um entweder per Username oder per ID zu laden.
     */
    @Transactional(readOnly = true)
    public User getByUsernameOrId(String username, Long id) {
        if (username != null) {
            return getByUsername(username);
        }
        if (id != null) {
            return getById(id);
        }
        throw new IllegalArgumentException("Either username or id must be provided");
    }

    /**
     * Prüft, ob Benutzername/Passwort-Kombination gültig ist.
     */
    @Transactional(readOnly = true)
    public User validateLogin(String username, String rawPassword) {
        User user = getByUsername(username);

        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        return user;
    }
}
