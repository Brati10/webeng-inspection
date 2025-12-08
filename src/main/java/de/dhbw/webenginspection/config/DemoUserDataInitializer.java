package de.dhbw.webenginspection.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import de.dhbw.webenginspection.entity.User;
import de.dhbw.webenginspection.entity.UserRole;
import de.dhbw.webenginspection.repository.UserRepository;

/**
 * Legt beim Start der Anwendung Demo-Benutzer an, falls diese noch nicht
 * existieren. Diese Benutzer können für manuelle Tests (z. B. Login) verwendet
 * werden.
 */
@Configuration
public class DemoUserDataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DemoUserDataInitializer.class);

    @Bean
    public CommandLineRunner demoUsersInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {

            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User("admin", "Markus Müller", passwordEncoder.encode("admin123"), UserRole.ADMIN);
                userRepository.save(admin);
                log.info("Created demo admin user with username 'admin' and password 'admin123'");
            } else {
                log.info("Demo admin user already exists, skipping creation.");
            }

            if (userRepository.findByUsername("inspector").isEmpty()) {
                User inspector = new User("inspector", "Laura Schmidt", passwordEncoder.encode("inspector123"),
                        UserRole.INSPECTOR);
                userRepository.save(inspector);
                log.info("Created demo inspector user with username 'inspector' and password 'inspector123'");
            } else {
                log.info("Demo inspector user already exists, skipping creation.");
            }

            if (userRepository.findByUsername("thomas.weber").isEmpty()) {
                User inspector = new User("thomas.weber", "Thomas Weber", passwordEncoder.encode("inspector123"),
                        UserRole.INSPECTOR);
                userRepository.save(inspector);
                log.info("Created demo inspector user with username 'thomas.weber' and password 'inspector123'");
            } else {
                log.info("Thomas Weber user already exists, skipping creation.");
            }
        };
    }
}
