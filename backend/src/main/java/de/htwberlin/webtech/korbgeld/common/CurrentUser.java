package de.htwberlin.webtech.korbgeld.common;

import org.springframework.security.oauth2.jwt.Jwt;

/** Liest die Nutzer-ID aus dem geprüften Token (Claim "sub"). */
public final class CurrentUser {

    private CurrentUser() {
    }

    public static Long id(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}
