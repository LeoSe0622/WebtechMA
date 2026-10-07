package de.htwberlin.webtech.korbgeld.common.seed;

import de.htwberlin.webtech.korbgeld.auth.SandboxService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Läuft einmal nach jedem Start. Flyway hat das Schema schon angelegt, hier kommen fehlende Daten dazu.
 * Jeder Schritt ist idempotent: Mehrfaches Ausführen erzeugt keine Duplikate.
 */
@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataSeeder implements ApplicationRunner {

    private final ProductCatalog productCatalog;
    private final PriceSeeder priceSeeder;
    private final LeaderboardSeeder leaderboardSeeder;
    private final SandboxService sandboxService;

    public DataSeeder(ProductCatalog productCatalog, PriceSeeder priceSeeder, LeaderboardSeeder leaderboardSeeder,
                      SandboxService sandboxService) {
        this.productCatalog = productCatalog;
        this.priceSeeder = priceSeeder;
        this.leaderboardSeeder = leaderboardSeeder;
        this.sandboxService = sandboxService;
    }

    @Override
    public void run(ApplicationArguments args) {
        productCatalog.ensureCatalog();
        priceSeeder.seed();
        leaderboardSeeder.seed();
        sandboxService.deleteExpiredSandboxUsers();
    }
}
