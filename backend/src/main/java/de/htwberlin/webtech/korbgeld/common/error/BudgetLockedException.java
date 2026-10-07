package de.htwberlin.webtech.korbgeld.common.error;

/** 409: Das Monatsbudget ist gesperrt, weil im Monat schon ein Einkauf erfasst ist. */
public class BudgetLockedException extends ConflictException {

    public BudgetLockedException() {
        super("Das Budget für diesen Monat ist gesperrt, weil schon ein Einkauf erfasst ist.");
    }
}
