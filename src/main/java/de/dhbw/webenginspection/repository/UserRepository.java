package de.dhbw.webenginspection.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import de.dhbw.webenginspection.entity.User;

/**
 * Repository für die Verwaltung von {@link User}-Entitäten.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Findet einen User anhand seines eindeutigen Usernamens.
     */
    Optional<User> findByUsername(String username);
}
