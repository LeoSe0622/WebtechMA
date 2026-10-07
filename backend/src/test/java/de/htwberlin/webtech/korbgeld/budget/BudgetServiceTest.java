package de.htwberlin.webtech.korbgeld.budget;

import de.htwberlin.webtech.korbgeld.auth.AppUser;
import de.htwberlin.webtech.korbgeld.auth.AppUserRepository;
import de.htwberlin.webtech.korbgeld.common.ClockConfig;
import de.htwberlin.webtech.korbgeld.common.error.BadRequestException;
import de.htwberlin.webtech.korbgeld.common.error.BudgetLockedException;
import de.htwberlin.webtech.korbgeld.common.error.ConflictException;
import de.htwberlin.webtech.korbgeld.shopping.PurchaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Reiner Unit-Test: Repositories sind Attrappen (Mockito), keine Datenbank, keine Spring-App. */
class BudgetServiceTest {

    private static final Long USER_ID = 1L;
    private static final YearMonth OCTOBER = YearMonth.of(2026, 10);

    private final MonthlyBudgetRepository budgetRepository = mock(MonthlyBudgetRepository.class);
    private final PurchaseRepository purchaseRepository = mock(PurchaseRepository.class);
    private final AppUserRepository appUserRepository = mock(AppUserRepository.class);
    private final Clock clock = Clock.fixed(LocalDate.of(2026, 10, 15).atStartOfDay(ClockConfig.ZONE).toInstant(),
            ClockConfig.ZONE);
    private BudgetService budgetService;

    @BeforeEach
    void setUp() {
        budgetService = new BudgetService(budgetRepository, purchaseRepository, appUserRepository, clock,
                new BigDecimal("500"));
        AppUser twoPersons = new AppUser("wg", null, "WG", 2, false, false, clock.instant());
        when(appUserRepository.findById(USER_ID)).thenReturn(Optional.of(twoPersons));
        when(budgetRepository.findByOwnerIdAndYearMonth(USER_ID, OCTOBER)).thenReturn(Optional.empty());
    }

    @Test
    void changingAnExistingBudgetIsRejectedOnceAPurchaseExistsInTheMonth() {
        when(budgetRepository.findByOwnerIdAndYearMonth(USER_ID, OCTOBER))
                .thenReturn(Optional.of(new MonthlyBudget(null, OCTOBER, new BigDecimal("250.00"))));
        when(purchaseRepository.existsByOwnerIdAndDateBetween(eq(USER_ID), any(), any())).thenReturn(true);

        assertThatThrownBy(() -> budgetService.set(USER_ID, OCTOBER, new BigDecimal("300.00")))
                .isInstanceOf(BudgetLockedException.class);
        verify(budgetRepository, never()).save(any());
    }

    @Test
    void aFirstBudgetMayStillBeCreatedAfterAPurchase() {
        // Wer zuerst einkauft, soll nicht in einer Sackgasse landen (Review 02, M2)
        when(purchaseRepository.existsByOwnerIdAndDateBetween(eq(USER_ID), any(), any())).thenReturn(true);
        when(budgetRepository.save(any())).thenAnswer(call -> call.getArgument(0));

        assertThat(budgetService.set(USER_ID, OCTOBER, new BigDecimal("300.00")).amount())
                .isEqualByComparingTo("300.00");
    }

    @Test
    void setBudgetRespectsTheLimitOf500PerPerson() {
        // Haushalt mit 2 Personen: höchstens 1.000 €
        assertThatThrownBy(() -> budgetService.set(USER_ID, OCTOBER, new BigDecimal("1000.01")))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("1000");

        assertThat(budgetService.set(USER_ID, OCTOBER, new BigDecimal("1000.00")).amount())
                .isEqualByComparingTo("1000.00");
    }

    @Test
    void setBudgetOnlyForCurrentAndNextMonth() {
        assertThatThrownBy(() -> budgetService.set(USER_ID, YearMonth.of(2026, 9), new BigDecimal("200")))
                .isInstanceOf(BadRequestException.class);
        assertThatThrownBy(() -> budgetService.set(USER_ID, YearMonth.of(2026, 12), new BigDecimal("200")))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void remainingMayBecomeNegative() {
        assertThat(BudgetMath.remaining(new BigDecimal("260.00"), new BigDecimal("275.60")))
                .isEqualByComparingTo("-15.60");
    }

    @Test
    void savingsRateIsRemainingDividedByBudgetAndNeverNegative() {
        assertThat(BudgetMath.savingsRate(new BigDecimal("260.00"), new BigDecimal("208.00")))
                .isEqualByComparingTo("0.2000");
        assertThat(BudgetMath.savingsRate(new BigDecimal("260.00"), new BigDecimal("300.00")))
                .isEqualByComparingTo("0");
    }
}
