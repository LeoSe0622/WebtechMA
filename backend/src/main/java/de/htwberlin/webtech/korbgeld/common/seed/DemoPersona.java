package de.htwberlin.webtech.korbgeld.common.seed;

import de.htwberlin.webtech.korbgeld.auth.AppUser;
import de.htwberlin.webtech.korbgeld.budget.MonthlyBudget;
import de.htwberlin.webtech.korbgeld.budget.MonthlyBudgetRepository;
import de.htwberlin.webtech.korbgeld.habit.Habit;
import de.htwberlin.webtech.korbgeld.habit.HabitRepository;
import de.htwberlin.webtech.korbgeld.pantry.PantryItem;
import de.htwberlin.webtech.korbgeld.pantry.PantryItemRepository;
import de.htwberlin.webtech.korbgeld.shopping.ListItem;
import de.htwberlin.webtech.korbgeld.shopping.ListItemRepository;
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
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Persona-Vorlage „Mia“ (AUFTRAG.md, Abschnitt 11): Studentin in einer WG, 260 € Budget.
 * Alle Daten entstehen relativ zum heutigen Datum, damit die Demo in jedem Monat frisch aussieht.
 */
@Component
public class DemoPersona {

    static final BigDecimal MONTHLY_BUDGET = new BigDecimal("260.00");

    // Sparquote je abgeschlossenem Monat, ältester zuerst. -0.06 = ein Monat über Budget.
    private static final double[] SAVING_RATES = {0.12, 0.05, 0.18, 0.00, 0.22, 0.09, -0.06, 0.15, 0.25, 0.08, 0.14, 0.20};
    private static final int[] PURCHASES_PER_MONTH = {4, 3, 5, 6, 3, 4, 6, 5, 3, 4, 5, 4};
    private static final List<String> STORES = List.of("Lidl Kreuzberg", "Rewe Oranienstraße", "Aldi Süd");

    private final ProductCatalog catalog;
    private final StoreRepository storeRepository;
    private final MonthlyBudgetRepository budgetRepository;
    private final PurchaseRepository purchaseRepository;
    private final PantryItemRepository pantryItemRepository;
    private final ListItemRepository listItemRepository;
    private final HabitRepository habitRepository;
    private final Clock clock;

    public DemoPersona(ProductCatalog catalog, StoreRepository storeRepository, MonthlyBudgetRepository budgetRepository,
                       PurchaseRepository purchaseRepository, PantryItemRepository pantryItemRepository,
                       ListItemRepository listItemRepository, HabitRepository habitRepository, Clock clock) {
        this.catalog = catalog;
        this.storeRepository = storeRepository;
        this.budgetRepository = budgetRepository;
        this.purchaseRepository = purchaseRepository;
        this.pantryItemRepository = pantryItemRepository;
        this.listItemRepository = listItemRepository;
        this.habitRepository = habitRepository;
        this.clock = clock;
    }

    @Transactional
    public void fillFor(AppUser user) {
        LocalDate today = LocalDate.now(clock);
        YearMonth currentMonth = YearMonth.from(today);
        Random random = new Random(42);   // fester Startwert: jede Demo sieht gleich aus

        List<Store> stores = new ArrayList<>();
        for (String name : STORES) {
            stores.add(storeRepository.save(new Store(user, name)));
        }

        // 12 abgeschlossene Monate Historie
        for (int i = 0; i < 12; i++) {
            YearMonth month = currentMonth.minusMonths(12 - i);
            budgetRepository.save(new MonthlyBudget(user, month, MONTHLY_BUDGET));
            BigDecimal spent = MONTHLY_BUDGET.multiply(BigDecimal.valueOf(1 - SAVING_RATES[i]))
                    .setScale(2, RoundingMode.HALF_UP);
            addPurchases(user, stores, month, spent, PURCHASES_PER_MONTH[i], month.lengthOfMonth(), random);
        }

        // Laufender Monat: Budget gesetzt, 2 Einkäufe bis heute
        budgetRepository.save(new MonthlyBudget(user, currentMonth, MONTHLY_BUDGET));
        addPurchases(user, stores, currentMonth, new BigDecimal("56.25"), 2, today.getDayOfMonth(), random);

        Instant now = clock.instant();
        // Vorrat: 12 Einträge, 2 laufen in ≤ 3 Tagen ab, 1 ist abgelaufen
        addPantry(user, "Hafermilch", 2, today.plusDays(10), now);
        addPantry(user, "Vollkornbrot", 1, today.plusDays(2), now);
        addPantry(user, "Naturjoghurt", 2, today.plusDays(1), now);
        addPantry(user, "Gouda in Scheiben", 1, today.minusDays(2), now);
        addPantry(user, "Spaghetti", 3, today.plusDays(300), now);
        addPantry(user, "Basmatireis", 1, today.plusDays(400), now);
        addPantry(user, "Tomaten passiert", 2, today.plusDays(200), now);
        addPantry(user, "Haferflocken", 1, today.plusDays(150), now);
        addPantry(user, "Äpfel", 6, today.plusDays(14), now);
        addPantry(user, "Möhren", 1, today.plusDays(9), now);
        addPantry(user, "Kidneybohnen", 2, null, now);
        addPantry(user, "Erdnussbutter", 1, today.plusDays(120), now);

        // Einkaufsliste: 6 Einträge, Hafermilch und Spaghetti stehen schon im Vorrat (Warnung sichtbar)
        String[] listProducts = {"Hafermilch", "Spaghetti", "Bananen", "Butter", "Kaffee gemahlen", "Paprika rot"};
        int[] listQuantities = {1, 2, 6, 1, 1, 2};
        for (int i = 0; i < listProducts.length; i++) {
            listItemRepository.save(new ListItem(user, catalog.get(listProducts[i]), listQuantities[i],
                    now.minusSeconds(600L - i * 60L)));
        }

        // 4 Gewohnheiten
        habitRepository.save(new Habit(user, catalog.get("Hafermilch"), 2, 7, today.plusDays(2)));
        habitRepository.save(new Habit(user, catalog.get("Haferflocken"), 1, 14, today.plusDays(5)));
        habitRepository.save(new Habit(user, catalog.get("Kaffee gemahlen"), 1, 30, today.plusDays(12)));
        habitRepository.save(new Habit(user, catalog.get("Bananen"), 6, 7, today.plusDays(1)));
    }

    private void addPurchases(AppUser user, List<Store> stores, YearMonth month, BigDecimal total, int count,
                              int maxDay, Random random) {
        List<BigDecimal> amounts = SeedPurchases.split(total, count, random);
        List<Integer> days = SeedPurchases.days(month, count, maxDay);
        List<String> names = catalog.names();
        for (int i = 0; i < count; i++) {
            List<PurchaseLine> lines = new ArrayList<>();
            int lineCount = 2 + random.nextInt(3);
            for (int j = 0; j < lineCount; j++) {
                String name = names.get(random.nextInt(names.size()));
                lines.add(new PurchaseLine(catalog.get(name).getId(), 1 + random.nextInt(2)));
            }
            purchaseRepository.save(new Purchase(user, stores.get(i % stores.size()), month.atDay(days.get(i)),
                    amounts.get(i), lines));
        }
    }

    private void addPantry(AppUser user, String productName, int quantity, LocalDate bestBefore, Instant addedAt) {
        pantryItemRepository.save(new PantryItem(user, catalog.get(productName), quantity, bestBefore, addedAt));
    }
}
