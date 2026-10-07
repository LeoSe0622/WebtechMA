package de.htwberlin.webtech.korbgeld.leaderboard;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

/**
 * Die Ranglisten-Regeln aus AUFTRAG.md, Abschnitt 7, als reine Funktionen (ohne Datenbank, leicht zu testen):
 * Sandbox-Nutzer ausschließen, nach Sparquote absteigend sortieren, bei Gleichstand nach Serie.
 */
public final class Ranking {

    private Ranking() {
    }

    public record Candidate(Long userId, String displayName, int householdSize, boolean sandbox,
                            BigDecimal savingsRate, int streak) {
    }

    // Höhere Sparquote zuerst; bei Gleichstand längere Serie; danach alphabetisch, damit die Reihenfolge stabil ist
    static final Comparator<Candidate> ORDER = Comparator
            .comparing(Candidate::savingsRate, Comparator.reverseOrder())
            .thenComparing(Candidate::streak, Comparator.reverseOrder())
            .thenComparing(Candidate::displayName);

    public static List<Candidate> rank(List<Candidate> candidates) {
        return candidates.stream()
                .filter(candidate -> !candidate.sandbox())
                .sorted(ORDER)
                .toList();
    }

    /** Platz, den jemand mit dieser Sparquote und Serie hätte, ohne selbst in der Liste zu stehen. */
    public static int positionFor(List<Candidate> ranked, BigDecimal savingsRate, int streak) {
        long better = ranked.stream()
                .filter(other -> other.savingsRate().compareTo(savingsRate) > 0
                        || (other.savingsRate().compareTo(savingsRate) == 0 && other.streak() > streak))
                .count();
        return (int) better + 1;
    }
}
