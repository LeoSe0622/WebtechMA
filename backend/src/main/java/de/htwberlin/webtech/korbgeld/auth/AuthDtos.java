package de.htwberlin.webtech.korbgeld.auth;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Die Datenformate (DTOs) für Login und Registrierung, gesammelt an einer Stelle. */
public final class AuthDtos {

    private AuthDtos() {
    }

    public record RegisterRequest(
            @NotBlank @Size(min = 3, max = 30)
            @Pattern(regexp = "[a-zA-Z0-9._-]+", message = "nur Buchstaben, Ziffern, Punkt, Unter- und Bindestrich")
            String username,
            @NotBlank @Size(min = 8, max = 72) String password,
            @NotBlank @Size(min = 3, max = 40) String displayName,
            @Min(1) @Max(6) Integer householdSize,
            Boolean leaderboardOptIn
    ) {
    }

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {
    }

    public record UserResponse(Long id, String username, String displayName, int householdSize,
                               boolean leaderboardOptIn, boolean sandbox) {

        static UserResponse from(AppUser user) {
            return new UserResponse(user.getId(), user.getUsername(), user.getDisplayName(),
                    user.getHouseholdSize(), user.isLeaderboardOptIn(), user.isSandbox());
        }
    }

    public record TokenResponse(String token, UserResponse user) {
    }
}
