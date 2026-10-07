package de.htwberlin.webtech.korbgeld.common.seed;

import de.htwberlin.webtech.korbgeld.TestcontainersConfiguration;
import de.htwberlin.webtech.korbgeld.auth.AppUser;
import de.htwberlin.webtech.korbgeld.auth.AppUserRepository;
import de.htwberlin.webtech.korbgeld.budget.MonthlyBudget;
import de.htwberlin.webtech.korbgeld.budget.MonthlyBudgetRepository;
import de.htwberlin.webtech.korbgeld.common.ClockConfig;
import de.htwberlin.webtech.korbgeld.shopping.PurchaseRepository;
import de.htwberlin.webtech.korbgeld.shopping.StoreRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Testcontainers(disabledWithoutDocker = true)
class LeaderboardSeederTest {

    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private MonthlyBudgetRepository budgetRepository;
    @Autowired
    private PurchaseRepository purchaseRepository;
    @Autowired
    private ProductCatalog catalog;

    @Test
    void fillsUpMissingMonthsWhenTheClockMovesOnWithoutDuplicates() {
        // Feste Uhr in der Zukunft, damit der Test unabhängig vom heutigen Datum ist
        Clock january = Clock.fixed(LocalDate.of(2031, 1, 15).atStartOfDay(ClockConfig.ZONE).toInstant(), ClockConfig.ZONE);
        Clock march = Clock.offset(january, java.time.Duration.ofDays(59));   // 15. März 2031

        seederWith(january).seed();
        seederWith(january).seed();   // zweiter Lauf im selben Monat: keine Änderung
        AppUser first = appUserRepository.findByUsername("seed-01").orElseThrow();
        assertThat(monthsOf(first)).contains(YearMonth.of(2030, 12)).doesNotContain(YearMonth.of(2031, 1));

        seederWith(march).seed();     // zwei Monate später

        List<YearMonth> months = monthsOf(first);
        assertThat(months).contains(YearMonth.of(2031, 1), YearMonth.of(2031, 2));
        assertThat(months).doesNotHaveDuplicates();
        assertThat(appUserRepository.findByUsername("seed-15")).isPresent();
    }

    private LeaderboardSeeder seederWith(Clock clock) {
        return new LeaderboardSeeder(appUserRepository, storeRepository, budgetRepository, purchaseRepository,
                catalog, clock);
    }

    private List<YearMonth> monthsOf(AppUser user) {
        return budgetRepository.findAllByOwnerIdOrderByYearMonthAsc(user.getId()).stream()
                .map(MonthlyBudget::getYearMonth)
                .toList();
    }
}
