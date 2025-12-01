package de.dhbw.webenginspection.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

import de.dhbw.webenginspection.repository.UserRepository;
import de.dhbw.webenginspection.service.CustomUserDetailsService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Zentrale Security-Konfiguration für Authentication und Authorization.
 * 
 * Security-Regeln: - Login (/api/auth/login): öffentlich - Alle anderen
 * Endpoints: authentifiziert - Spezifische Rollen-Authorisierungen
 * per @PreAuthorize auf Controller-Methoden
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()).cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/**").authenticated().requestMatchers("/api/inspections/**")
                        .authenticated().requestMatchers("/api/checklists/**").authenticated()
                        .requestMatchers("/api/checklist-steps/**").authenticated()
                        .requestMatchers("/api/inspection-steps/**").authenticated().requestMatchers("/api/**")
                        .authenticated().anyRequest().permitAll())
                .sessionManagement(session -> session.sessionConcurrency(concurrency -> concurrency.maximumSessions(1)))
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, UserDetailsService userDetailsService)
            throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http
                .getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService(userRepository);
    }

    /**
     * PasswordEncoder-Bean für sicheres Speichern und Prüfen von Passwörtern.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}