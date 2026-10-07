package de.htwberlin.webtech.korbgeld.auth;

import de.htwberlin.webtech.korbgeld.common.ClockConfig;
import de.htwberlin.webtech.korbgeld.common.error.DemoLimitReachedException;
import de.htwberlin.webtech.korbgeld.common.seed.DemoPersona;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/** Demo-Zugänge: Jeder Demo-Login bekommt einen eigenen Sandbox-Nutzer mit Beispieldaten von „Mia“. */
@Service
public class SandboxService {

    private static final Logger log = LoggerFactory.getLogger(SandboxService.class);

    private final AppUserRepository appUserRepository;
    private final DemoPersona demoPersona;
    private final Clock clock;
    private final int maxPerDay;
    private final int retentionDays;

    public SandboxService(AppUserRepository appUserRepository, DemoPersona demoPersona, Clock clock,
                          @Value("${app.demo.max-per-day}") int maxPerDay,
                          @Value("${app.demo.retention-days}") int retentionDays) {
        this.appUserRepository = appUserRepository;
        this.demoPersona = demoPersona;
        this.clock = clock;
        this.maxPerDay = maxPerDay;
        this.retentionDays = retentionDays;
    }

    @Transactional
    public AppUser createSandboxUser() {
        // Aufräumen bei jedem Demo-Login: Der tägliche Job allein reicht nicht, weil Render einschläft
        deleteExpiredSandboxUsers();

        Instant startOfToday = LocalDate.now(clock).atStartOfDay(ClockConfig.ZONE).toInstant();
        if (appUserRepository.countBySandboxTrueAndCreatedAtAfter(startOfToday) >= maxPerDay) {
            throw new DemoLimitReachedException();
        }

        String username = "demo-" + UUID.randomUUID().toString().substring(0, 8);
        AppUser user = appUserRepository.save(
                new AppUser(username, null, "Mia (Demo)", 1, false, true, clock.instant()));
        demoPersona.fillFor(user);
        return user;
    }

    // Täglich um 4 Uhr, zusätzlich beim Start (DataSeeder) und bei jedem Demo-Login
    @Scheduled(cron = "0 0 4 * * *", zone = "Europe/Berlin")
    @Transactional
    public void deleteExpiredSandboxUsers() {
        Instant cutoff = clock.instant().minus(retentionDays, ChronoUnit.DAYS);
        int deleted = appUserRepository.deleteSandboxUsersCreatedBefore(cutoff);
        if (deleted > 0) {
            log.info("{} Sandbox-Nutzer gelöscht (älter als {} Tage)", deleted, retentionDays);
        }
    }
}
