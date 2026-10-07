package de.htwberlin.webtech.korbgeld.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "app_user")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String username;

    // Nur der BCrypt-Hash, nie das Passwort. NULL bei Seed- und Sandbox-Nutzern.
    @Column(length = 100)
    private String passwordHash;

    @Column(nullable = false, length = 40)
    private String displayName;

    @Column(nullable = false)
    private int householdSize;

    @Column(nullable = false)
    private boolean leaderboardOptIn;

    @Column(nullable = false)
    private boolean sandbox;

    @Column(nullable = false)
    private Instant createdAt;

    protected AppUser() {
        // für JPA
    }

    public AppUser(String username, String passwordHash, String displayName, int householdSize,
                   boolean leaderboardOptIn, boolean sandbox, Instant createdAt) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.displayName = displayName;
        this.householdSize = householdSize;
        this.leaderboardOptIn = leaderboardOptIn;
        this.sandbox = sandbox;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getHouseholdSize() {
        return householdSize;
    }

    public boolean isLeaderboardOptIn() {
        return leaderboardOptIn;
    }

    public boolean isSandbox() {
        return sandbox;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
