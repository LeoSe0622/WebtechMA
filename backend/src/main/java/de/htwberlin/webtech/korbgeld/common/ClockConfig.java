package de.htwberlin.webtech.korbgeld.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
@EnableScheduling
public class ClockConfig {

    /** Alle Monats- und Datumsrechnungen laufen in deutscher Zeit, auch auf Servern in UTC. */
    public static final ZoneId ZONE = ZoneId.of("Europe/Berlin");

    // Als Bean, damit Tests eine feste Uhr einsetzen können (z. B. Seeder-Test)
    @Bean
    public Clock clock() {
        return Clock.system(ZONE);
    }
}
