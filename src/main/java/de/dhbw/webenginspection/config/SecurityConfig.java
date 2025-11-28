package de.dhbw.webenginspection.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Zentrale Security-Konfiguration. Aktuell sind alle Endpunkte noch
 * freigegeben, damit bestehende Funktionen nicht beeinträchtigt werden.
 * Security wird primär für Passwort-Hashing und spätere Erweiterungen genutzt.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Für einfache REST-APIs deaktivieren wir CSRF (kann später
                // verfeinert werden)
                .csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth
                        // Auth-Endpunkte und H2-Console explizit zulassen
                        .requestMatchers("/api/auth/**", "/h2-console/**").permitAll()
                        // alles andere aktuell auch noch offen
                        .anyRequest().permitAll())
                // für H2-Console nötig
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    /**
     * PasswordEncoder-Bean für sicheres Speichern und Prüfen von Passwörtern.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
