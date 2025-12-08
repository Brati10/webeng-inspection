package de.dhbw.webenginspection.service;

import java.util.List;
import java.util.Optional;

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
     * Legt einen neuen Benutzer mit gehashtem Passwort an oder gibt den
     * existierenden zurück. Diese Methode ist idempotent - wird mit denselben
     * Parametern aufgerufen, wird immer der gleiche User zurückgegeben.
     *
     * @param username eindeutiger Benutzername
     * @param displayName Anzeigename des Benutzers
     * @param rawPassword unverschlüsseltes Passwort (wird gehashed)
     * @param role {@link UserRole} des Benutzers
     * @return der erstellte oder bereits existierende {@link User}
     */
    public User createUser(String username, String displayName, String rawPassword, UserRole role) {
        log.info("Creating or retrieving user with username {}", username);

        Optional<User> existing = userRepository.findByUsername(username);
        if (existing.isPresent()) {
            log.info("User with username {} already exists, returning existing user", username);
            return existing.get();
        }

        String passwordHash = passwordEncoder.encode(rawPassword);
        User user = new User(username, displayName, passwordHash, role);
        User saved = userRepository.save(user);
        log.info("Created new user with id {}", saved.getId());

        return saved;
    }

    /**
     * Liefert alle Benutzer zurück.
     *
     * @return eine Liste aller {@link User}-Entitäten (niemals {@code null})
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll();
    }

    /**
     * Sucht einen Benutzer anhand der ID.
     *
     * @param id die eindeutige Benutzer-ID
     * @return ein Optional mit dem gefundenen {@link User}, oder leer wenn
     * nicht existiert
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        log.info("Fetching user with id {}", id);
        return userRepository.findById(id);
    }

    /**
     * Sucht einen Benutzer anhand des Usernamens.
     *
     * @param username der eindeutige Benutzername
     * @return ein Optional mit dem gefundenen {@link User}, oder leer wenn
     * nicht existiert
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        log.info("Fetching user with username {}", username);
        return userRepository.findByUsername(username);
    }

    /**
     * Prüft, ob Benutzername/Passwort-Kombination gültig ist.
     *
     * @param username der Benutzername
     * @param rawPassword das unverschlüsselte Passwort
     * @return der authentifizierte {@link User}
     * @throws IllegalArgumentException wenn Username oder Passwort ungültig
     * sind
     */
    @Transactional(readOnly = true)
    public User validateLogin(String username, String rawPassword) {
        log.info("Validating login for user {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            log.warn("Invalid password attempt for user {}", username);
            throw new IllegalArgumentException("Invalid username or password");
        }

        log.info("Login successful for user {}", username);
        return user;
    }

    /**
     * Löscht einen Benutzer anhand seiner ID.
     *
     * @param id die ID des zu löschenden Benutzers
     * @throws IllegalArgumentException wenn kein Benutzer mit der ID existiert
     */
    public void deleteUser(Long id) {
        log.info("Deleting user with id {}", id);

        if (!userRepository.existsById(id)) {
            log.warn("User with id {} not found for deletion", id);
            throw new IllegalArgumentException("User with id " + id + " not found");
        }

        userRepository.deleteById(id);
        log.info("Deleted user with id {}", id);
    }
}