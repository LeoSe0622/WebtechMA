package de.htwberlin.webtech.korbgeld.budget;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.YearMonth;

public final class BudgetDtos {

    private BudgetDtos() {
    }

    public record BudgetRequest(
            @NotNull @DecimalMin(value = "1.00", message = "muss mindestens 1 € sein")
            @Digits(integer = 6, fraction = 2) BigDecimal amount
    ) {
    }

    public record BudgetResponse(YearMonth yearMonth, BigDecimal amount, boolean locked) {
    }

    /**
     * Übersicht für Dashboard und Kassenbon-Streifen. amount und remaining sind null, wenn für den Monat
     * noch kein Budget gesetzt ist. lastCompleted ist der Vormonat mit seiner (endgültigen) Sparquote.
     */
    public record BudgetSummaryResponse(YearMonth yearMonth, BigDecimal amount, BigDecimal spent, BigDecimal remaining,
                                        boolean locked, MonthResult lastCompleted) {
    }

    public record MonthResult(YearMonth yearMonth, BigDecimal amount, BigDecimal spent, BigDecimal remaining,
                              BigDecimal savingsRate) {
    }
}
