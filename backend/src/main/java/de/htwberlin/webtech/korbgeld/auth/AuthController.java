package de.htwberlin.webtech.korbgeld.auth;

import de.htwberlin.webtech.korbgeld.auth.AuthDtos.LoginRequest;
import de.htwberlin.webtech.korbgeld.auth.AuthDtos.RegisterRequest;
import de.htwberlin.webtech.korbgeld.auth.AuthDtos.TokenResponse;
import de.htwberlin.webtech.korbgeld.auth.AuthDtos.UserResponse;
import de.htwberlin.webtech.korbgeld.common.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/auth/register")
    @ResponseStatus(HttpStatus.CREATED)
    public TokenResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/auth/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    // Legt einen frischen Sandbox-Nutzer mit Beispieldaten an, ohne Passwort
    @PostMapping("/auth/demo")
    @ResponseStatus(HttpStatus.CREATED)
    public TokenResponse demo() {
        return authService.demo();
    }

    // @AuthenticationPrincipal: Spring übergibt das geprüfte Token des aktuellen Requests
    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal Jwt jwt) {
        return authService.me(CurrentUser.id(jwt));
    }
}
