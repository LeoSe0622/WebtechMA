package de.htwberlin.webtech.korbgeld.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/** Stellt das JWT aus, das das Frontend bei jeder Anfrage im Authorization-Header mitschickt. */
@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final Clock clock;
    private final long validityHours;

    public TokenService(JwtEncoder jwtEncoder, Clock clock, @Value("${app.jwt.validity-hours}") long validityHours) {
        this.jwtEncoder = jwtEncoder;
        this.clock = clock;
        this.validityHours = validityHours;
    }

    public String createToken(AppUser user) {
        Instant now = clock.instant();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("korbgeld")
                .subject(user.getId().toString())        // daraus liest CurrentUser später die Nutzer-ID
                .issuedAt(now)
                .expiresAt(now.plus(validityHours, ChronoUnit.HOURS))
                .claim("name", user.getDisplayName())
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
