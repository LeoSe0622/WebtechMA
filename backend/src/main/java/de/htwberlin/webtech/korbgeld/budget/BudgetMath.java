package de.htwberlin.webtech.korbgeld.budget;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Die Rechenregeln aus AUFTRAG.md, Abschnitt 7, als reine Funktionen (leicht zu testen). */
public final class BudgetMath {

    private BudgetMath() {
    }

    /** Restbudget = Budget minus Summe der Einkäufe. Darf negativ werden. */
    public static BigDecimal remaining(BigDecimal amount, BigDecimal spent) {
        return amount.subtract(spent);
    }

    /** Sparquote = max(0, Restbudget) ÷ Budget, als Anteil zwischen 0 und 1 mit 4 Nachkommastellen. */
    public static BigDecimal savingsRate(BigDecimal amount, BigDecimal spent) {
        BigDecimal saved = remaining(amount, spent).max(BigDecimal.ZERO);
        return saved.divide(amount, 4, RoundingMode.HALF_UP);
    }

    /** Obergrenze = Betrag pro Person × Haushaltsgröße. */
    public static BigDecimal maxBudget(BigDecimal maxPerPerson, int householdSize) {
        return maxPerPerson.multiply(BigDecimal.valueOf(householdSize));
    }
}
