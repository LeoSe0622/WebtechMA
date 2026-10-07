package de.htwberlin.webtech.korbgeld.common.seed;

import de.htwberlin.webtech.korbgeld.auth.AppUser;
import de.htwberlin.webtech.korbgeld.auth.AppUserRepository;
import de.htwberlin.webtech.korbgeld.budget.MonthlyBudget;
import de.htwberlin.webtech.korbgeld.budget.MonthlyBudgetRepository;
import de.htwberlin.webtech.korbgeld.shopping.Purchase;
import de.htwberlin.webtech.korbgeld.shopping.PurchaseLine;
import de.htwberlin.webtech.korbgeld.shopping.PurchaseRepository;
import de.htwberlin.webtech.korbgeld.shopping.Store;
import de.htwberlin.webtech.korbgeld.shopping.StoreRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.YearMonth;
import java.util.List;
import java.util.Random;

/**
 * 15 feste Ranglisten-Nutzer mit je 6 abgeschlossenen Monaten. Bei jedem Start werden fehlende Monate
 * bis zum letzten abgeschlossenen Monat ergänzt, sonst wäre die Rangliste im Januar leer (Review 00, M3).
 */
@Component
public class LeaderboardSeeder {

    static final List<String> PSEUDONYMS = List.of(
            "Sparfuchs Kreuzberg", "Kassenbon-Königin", "Rabattritter", "WG Wedding", "Budget-Bär",
            "Linsenliebe", "Pfandheld", "Angebotsjäger", "Restekoch", "Einkaufszettel-Profi",
            "Mensa-Meidende", "Haferflocken-Fan", "Sparschwein Neukölln", "Wochenmarkt-Wilma", "Discount-Duo");

    private static final BigDecimal BUDGET_PER_PERSON = new BigDecimal("230.00");
    private static final int MONTHS = 6;

    private final AppUserRepository appUserRepository;
    private final StoreRepository storeRepository;
    private final MonthlyBudgetRepository budgetRepository;
    private final PurchaseRepository purchaseRepository;
    private final ProductCatalog catalog;
    private final Clock clock;

    public LeaderboardSeeder(AppUserRepository appUserRepository, StoreRepository storeRepository,
                             MonthlyBudgetRepository budgetRepository, PurchaseRepository purchaseRepository,
                             ProductCatalog catalog, Clock clock) {
        this.appUserRepository = appUserRepository;
        this.storeRepository = storeRepository;
        this.budgetRepository = budgetRepository;
        this.purchaseRepository = purchaseRepository;
        this.catalog = catalog;
        this.clock = clock;
    }

    @Transactional
    public void seed() {
        YearMonth lastCompleted = YearMonth.now(clock).minusMonths(1);
        for (int i = 0; i < PSEUDONYMS.size(); i++) {
            AppUser user = findOrCreateUser(i);
            Store store = storeRepository.findByOwnerIdAndNameIgnoreCase(user.getId(), "Supermarkt")
                    .orElseGet(() -> storeRepository.save(new Store(user, "Supermarkt")));
            for (int m = MONTHS - 1; m >= 0; m--) {
                YearMonth month = lastCompleted.minusMonths(m);
                // Idempotent: Monate, die schon da sind, bleiben unverändert
                if (!budgetRepository.existsByOwnerIdAndYearMonth(user.getId(), month)) {
                    addMonth(user, store, month, i);
                }
            }
        }
    }

    private AppUser findOrCreateUser(int index) {
        String username = String.format("seed-%02d", index + 1);
        return appUserRepository.findByUsername(username).orElseGet(() -> appUserRepository.save(
                // kein Passwort: Seed-Nutzer können sich nicht anmelden
                new AppUser(username, null, PSEUDONYMS.get(index), index % 4 + 1, true, false, clock.instant())));
    }

    private void addMonth(AppUser user, Store store, YearMonth month, int index) {
        BigDecimal budget = BUDGET_PER_PERSON.multiply(BigDecimal.valueOf(user.getHouseholdSize()));
        budgetRepository.save(new MonthlyBudget(user, month, budget));

        // Gleicher Nutzer + gleicher Monat ergibt immer dieselbe Sparquote zwischen -5 % und 30 %
        Random random = new Random(index * 7919L + month.getYear() * 12L + month.getMonthValue());
        double savingRate = -0.05 + random.nextDouble() * 0.35;
        BigDecimal spent = budget.multiply(BigDecimal.valueOf(1 - savingRate)).setScale(2, RoundingMode.HALF_UP);

        List<BigDecimal> amounts = SeedPurchases.split(spent, 2, random);
        List<Integer> days = List.of(5, 20);
        Long productId = catalog.get(catalog.names().get(index % catalog.names().size())).getId();
        for (int p = 0; p < amounts.size(); p++) {
            purchaseRepository.save(new Purchase(user, store, month.atDay(days.get(p)), amounts.get(p),
                    List.of(new PurchaseLine(productId, 1))));
        }
    }
}
