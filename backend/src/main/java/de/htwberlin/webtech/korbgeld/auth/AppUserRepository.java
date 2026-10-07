package de.htwberlin.webtech.korbgeld.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByUsername(String username);

    boolean existsByUsername(String username);

    long countBySandboxTrueAndCreatedAtAfter(Instant since);

    List<AppUser> findByLeaderboardOptInTrue();

    // Ein DELETE für alle alten Sandbox-Nutzer; ihre Daten löscht die Datenbank per ON DELETE CASCADE mit
    @Modifying
    @Query("delete from AppUser u where u.sandbox = true and u.createdAt < :cutoff")
    int deleteSandboxUsersCreatedBefore(Instant cutoff);
}
