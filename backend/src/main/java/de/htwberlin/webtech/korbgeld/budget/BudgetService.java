package de.htwberlin.webtech.korbgeld.budget;

import de.htwberlin.webtech.korbgeld.auth.AppUser;
import de.htwberlin.webtech.korbgeld.auth.AppUserRepository;
import de.htwberlin.webtech.korbgeld.budget.BudgetDtos.BudgetResponse;
import de.htwberlin.webtech.korbgeld.budget.BudgetDtos.BudgetSummaryResponse;
import de.htwberlin.webtech.korbgeld.budget.BudgetDtos.MonthResult;
import de.htwberlin.webtech.korbgeld.common.error.BadRequestException;
import de.htwberlin.webtech.korbgeld.common.error.BudgetLockedException;
import de.htwberlin.webtech.korbgeld.common.error.ConflictException;
import de.htwberlin.webtech.korbgeld.common.error.NotFoundException;
import de.htwberlin.webtech.korbgeld.shopping.PurchaseRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.YearMonth;
import java.util.Optional;

@Service
public class BudgetService {

    private final MonthlyBudgetRepository budgetRepository;
    private final PurchaseRepository purchaseRepository;
    private final AppUserRepository appUserRepository;
    private final Clock clock;
    private final BigDecimal maxPerPerson;

    public BudgetService(MonthlyBudgetRepository budgetRepository, PurchaseRepository purchaseRepository,
                         AppUserRepository appUserRepository, Clock clock,
                         @Value("${app.budget.max-per-person}") BigDecimal maxPerPerson) {
        this.budgetRepository = budgetRepository;
        this.purchaseRepository = purchaseRepository;
        this.appUserRepository = appUserRepository;
        this.clock = clock;
        this.maxPerPerson = maxPerPerson;
    }

    @Transactional(readOnly = true)
    public BudgetResponse get(Long userId, YearMonth month) {
        MonthlyBudget budget = budgetRepository.findByOwnerIdAndYearMonth(userId, month)
                .orElseThrow(() -> new NotFoundException("Für " + month + " ist noch kein Budget angelegt."));
        return new BudgetResponse(month, budget.getAmount(), hasPurchases(userId, month));
    }

    @Transactional
    public BudgetResponse set(Long userId, YearMonth month, BigDecimal amount) {
        YearMonth current = YearMonth.now(clock);
        if (month.isBefore(current) || month.isAfter(current.plusMonths(1))) {
            throw new BadRequestException("Ein Budget kannst du nur für diesen und den nächsten Monat festlegen.");
        }
        // Sperre: Ein vorhandenes Budget ist nur änderbar, bis im Monat der erste Einkauf erfasst ist.
        // Ein erstes Budget darf man auch danach noch anlegen (Review 02, M2; E15).
        Optional<MonthlyBudget> existing = budgetRepository.findByOwnerIdAndYearMonth(userId, month);
        if (existing.isPresent() && hasPurchases(userId, month)) {
            throw new BudgetLockedException();
        }
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Nutzer nicht gefunden."));
        BigDecimal max = BudgetMath.maxBudget(maxPerPerson, user.getHouseholdSize());
        if (amount.compareTo(max) > 0) {
            throw new ConflictException("Das Budget darf höchstens " + max + " € betragen ("
                    + maxPerPerson + " € pro Person im Haushalt).");
        }

        MonthlyBudget budget = existing.orElseGet(() -> new MonthlyBudget(user, month, amount));
        budget.changeAmount(amount);
        budgetRepository.save(budget);
        // Erstes Budget nach einem Einkauf: ab sofort gesperrt, das soll die Antwort auch sagen
        return new BudgetResponse(month, budget.getAmount(), hasPurchases(userId, month));
    }

    @Transactional(readOnly = true)
    public BudgetSummaryResponse currentSummary(Long userId) {
        YearMonth current = YearMonth.now(clock);
        BigDecimal spent = spentIn(userId, current);
        BigDecimal amount = budgetRepository.findByOwnerIdAndYearMonth(userId, current)
                .map(MonthlyBudget::getAmount).orElse(null);
        BigDecimal remaining = amount == null ? null : BudgetMath.remaining(amount, spent);
        boolean locked = amount != null && hasPurchases(userId, current);
        return new BudgetSummaryResponse(current, amount, spent, remaining, locked,
                lastCompleted(userId, current.minusMonths(1)));
    }

    /** Restbudget des Monats oder null ohne Budget; für die Antwort nach einem Einkauf. */
    @Transactional(readOnly = true)
    public BigDecimal remaining(Long userId, YearMonth month) {
        return budgetRepository.findByOwnerIdAndYearMonth(userId, month)
                .map(budget -> BudgetMath.remaining(budget.getAmount(), spentIn(userId, month)))
                .orElse(null);
    }

    private MonthResult lastCompleted(Long userId, YearMonth month) {
        return budgetRepository.findByOwnerIdAndYearMonth(userId, month)
                .map(budget -> {
                    BigDecimal spent = spentIn(userId, month);
                    return new MonthResult(month, budget.getAmount(), spent,
                            BudgetMath.remaining(budget.getAmount(), spent),
                            BudgetMath.savingsRate(budget.getAmount(), spent));
                })
                .orElse(null);
    }

    private boolean hasPurchases(Long userId, YearMonth month) {
        return purchaseRepository.existsByOwnerIdAndDateBetween(userId, month.atDay(1), month.atEndOfMonth());
    }

    private BigDecimal spentIn(Long userId, YearMonth month) {
        return purchaseRepository.sumTotalAmount(userId, month.atDay(1), month.atEndOfMonth());
    }
}
