package de.htwberlin.webtech.korbgeld.auth;

import de.htwberlin.webtech.korbgeld.auth.AuthDtos.LoginRequest;
import de.htwberlin.webtech.korbgeld.auth.AuthDtos.RegisterRequest;
import de.htwberlin.webtech.korbgeld.auth.AuthDtos.TokenResponse;
import de.htwberlin.webtech.korbgeld.auth.AuthDtos.UserResponse;
import de.htwberlin.webtech.korbgeld.common.error.ConflictException;
import de.htwberlin.webtech.korbgeld.common.error.InvalidCredentialsException;
import de.htwberlin.webtech.korbgeld.common.error.NotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final SandboxService sandboxService;
    private final Clock clock;

    public AuthService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder,
                       TokenService tokenService, SandboxService sandboxService, Clock clock) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.sandboxService = sandboxService;
        this.clock = clock;
    }

    @Transactional
    public TokenResponse register(RegisterRequest request) {
        if (appUserRepository.existsByUsername(request.username())) {
            throw new ConflictException("Dieser Benutzername ist schon vergeben.");
        }
        int householdSize = request.householdSize() == null ? 1 : request.householdSize();
        boolean optIn = Boolean.TRUE.equals(request.leaderboardOptIn());
        AppUser user = appUserRepository.save(new AppUser(request.username(),
                passwordEncoder.encode(request.password()),     // nur der BCrypt-Hash wird gespeichert
                request.displayName(), householdSize, optIn, false, clock.instant()));
        return tokenFor(user);
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        AppUser user = appUserRepository.findByUsername(request.username())
                .orElseThrow(InvalidCredentialsException::new);
        // Seed- und Sandbox-Nutzer haben kein Passwort und können sich so nicht anmelden
        if (user.getPasswordHash() == null || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        return tokenFor(user);
    }

    @Transactional
    public TokenResponse demo() {
        return tokenFor(sandboxService.createSandboxUser());
    }

    @Transactional(readOnly = true)
    public UserResponse me(Long userId) {
        return appUserRepository.findById(userId)
                .map(UserResponse::from)
                .orElseThrow(() -> new NotFoundException("Nutzer nicht gefunden."));
    }

    private TokenResponse tokenFor(AppUser user) {
        return new TokenResponse(tokenService.createToken(user), UserResponse.from(user));
    }
}
