package de.htwberlin.webtech.korbgeld.leaderboard;

import de.htwberlin.webtech.korbgeld.leaderboard.Ranking.Candidate;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RankingTest {

    private static Candidate candidate(String name, String rate, int streak, boolean sandbox) {
        return new Candidate((long) name.hashCode(), name, 1, sandbox, new BigDecimal(rate), streak);
    }

    @Test
    void sortsBySavingsRateDescending() {
        List<Candidate> ranked = Ranking.rank(List.of(
                candidate("Mitte", "0.1000", 1, false),
                candidate("Oben", "0.2500", 1, false),
                candidate("Unten", "0.0000", 1, false)));

        assertThat(ranked).extracting(Candidate::displayName).containsExactly("Oben", "Mitte", "Unten");
    }

    @Test
    void tieIsBrokenByLongerStreak() {
        List<Candidate> ranked = Ranking.rank(List.of(
                candidate("Kurze Serie", "0.1500", 2, false),
                candidate("Lange Serie", "0.1500", 5, false)));

        assertThat(ranked).extracting(Candidate::displayName).containsExactly("Lange Serie", "Kurze Serie");
    }

    @Test
    void sandboxUsersAreExcluded() {
        List<Candidate> ranked = Ranking.rank(List.of(
                candidate("Echt", "0.1000", 1, false),
                candidate("Demo", "0.9000", 9, true)));

        assertThat(ranked).extracting(Candidate::displayName).containsExactly("Echt");
    }

    @Test
    void positionForCountsOnlyStrictlyBetterEntries() {
        List<Candidate> ranked = Ranking.rank(List.of(
                candidate("A", "0.3000", 1, false),
                candidate("B", "0.2000", 4, false),
                candidate("C", "0.2000", 1, false),
                candidate("D", "0.0500", 1, false)));

        assertThat(Ranking.positionFor(ranked, new BigDecimal("0.2000"), 2)).isEqualTo(3);   // hinter A und B
        assertThat(Ranking.positionFor(ranked, new BigDecimal("0.5000"), 0)).isEqualTo(1);
        assertThat(Ranking.positionFor(ranked, BigDecimal.ZERO, 0)).isEqualTo(5);
    }
}
