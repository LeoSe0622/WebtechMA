package de.htwberlin.webtech.korbgeld.leaderboard;

import de.htwberlin.webtech.korbgeld.auth.AppUser;
import de.htwberlin.webtech.korbgeld.auth.AppUserRepository;
import de.htwberlin.webtech.korbgeld.budget.BudgetMath;
import de.htwberlin.webtech.korbgeld.budget.MonthlyBudget;
import de.htwberlin.webtech.korbgeld.budget.MonthlyBudgetRepository;
import de.htwberlin.webtech.korbgeld.leaderboard.Ranking.Candidate;
import de.htwberlin.webtech.korbgeld.shopping.PurchaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LeaderboardService {

    public record Entry(int rank, String displayName, int householdSize, BigDecimal savingsRate, int streak) {
    }

    /** yourPosition ist null, wenn der eigene letzte Monat kein Budget hatte. */
    public record LeaderboardResponse(YearMonth month, List<Entry> entries, Integer yourPosition,
                                      BigDecimal yourSavingsRate, int yourStreak) {
    }

    private static final int MAX_STREAK_MONTHS = 24;

    private final AppUserRepository appUserRepository;
    private final MonthlyBudgetRepository budgetRepository;
    private final PurchaseRepository purchaseRepository;
    private final Clock clock;

    public LeaderboardService(AppUserRepository appUserRepository, MonthlyBudgetRepository budgetRepository,
                              PurchaseRepository purchaseRepository, Clock clock) {
        this.appUserRepository = appUserRepository;
        this.budgetRepository = budgetRepository;
        this.purchaseRepository = purchaseRepository;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public LeaderboardResponse leaderboard(Long currentUserId) {
        // Gewertet wird der letzte abgeschlossene Monat; der laufende ist nur vorläufig
        YearMonth month = YearMonth.now(clock).minusMonths(1);

        List<Candidate> candidates = new ArrayList<>();
        // Sandbox-Nutzer filtert Ranking.rank heraus (eine Stelle, im RankingTest geprüft)
        for (AppUser user : appUserRepository.findByLeaderboardOptInTrue()) {
            if (!user.getId().equals(currentUserId)) {   // man selbst steht nie in der Liste
                candidateFor(user, month).ifPresent(candidates::add);
            }
        }
        List<Candidate> ranked = Ranking.rank(candidates);

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < ranked.size(); i++) {
            Candidate c = ranked.get(i);
            entries.add(new Entry(i + 1, c.displayName(), c.householdSize(), c.savingsRate(), c.streak()));
        }

        AppUser me = appUserRepository.findById(currentUserId).orElseThrow();
        Optional<Candidate> mine = candidateFor(me, month);
        return new LeaderboardResponse(month, entries,
                mine.map(c -> Ranking.positionFor(ranked, c.savingsRate(), c.streak())).orElse(null),
                mine.map(Candidate::savingsRate).orElse(null),
                mine.map(Candidate::streak).orElse(0));
    }

    private Optional<Candidate> candidateFor(AppUser user, YearMonth month) {
        return budgetRepository.findByOwnerIdAndYearMonth(user.getId(), month)
                .map(budget -> new Candidate(user.getId(), user.getDisplayName(), user.getHouseholdSize(),
                        user.isSandbox(), BudgetMath.savingsRate(budget.getAmount(), spent(user.getId(), month)),
                        streak(user.getId(), month)));
    }

    /** Serie: aufeinanderfolgende abgeschlossene Monate bis einschließlich month, mit Budget und nicht darüber. */
    int streak(Long userId, YearMonth month) {
        int streak = 0;
        YearMonth current = month;
        while (streak < MAX_STREAK_MONTHS) {
            Optional<MonthlyBudget> budget = budgetRepository.findByOwnerIdAndYearMonth(userId, current);
            if (budget.isEmpty() || spent(userId, current).compareTo(budget.get().getAmount()) > 0) {
                break;
            }
            streak++;
            current = current.minusMonths(1);
        }
        return streak;
    }

    private BigDecimal spent(Long userId, YearMonth month) {
        return purchaseRepository.sumTotalAmount(userId, month.atDay(1), month.atEndOfMonth());
    }
}
