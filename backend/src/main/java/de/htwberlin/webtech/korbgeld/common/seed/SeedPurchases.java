package de.htwberlin.webtech.korbgeld.common.seed;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Hilfsrechnungen für Seed-Daten: Monatssumme auf mehrere Einkäufe verteilen. */
final class SeedPurchases {

    private SeedPurchases() {
    }

    /** Teilt total in count Beträge auf. Die Summe stimmt auf den Cent genau. */
    static List<BigDecimal> split(BigDecimal total, int count, Random random) {
        double[] weights = new double[count];
        double sum = 0;
        for (int i = 0; i < count; i++) {
            weights[i] = 0.6 + random.nextDouble() * 0.8;
            sum += weights[i];
        }
        List<BigDecimal> parts = new ArrayList<>();
        BigDecimal assigned = BigDecimal.ZERO;
        for (int i = 0; i < count - 1; i++) {
            BigDecimal part = total.multiply(BigDecimal.valueOf(weights[i] / sum)).setScale(2, RoundingMode.HALF_UP);
            parts.add(part);
            assigned = assigned.add(part);
        }
        parts.add(total.subtract(assigned));   // der letzte Einkauf gleicht Rundungscents aus
        return parts;
    }

    /** Verteilt count Einkaufstage gleichmäßig über den Monat (Tag 1 bis maxDay). */
    static List<Integer> days(YearMonth month, int count, int maxDay) {
        int lastDay = Math.min(maxDay, month.lengthOfMonth());
        List<Integer> days = new ArrayList<>();
        if (count == 1) {
            days.add(lastDay);
            return days;
        }
        for (int i = 0; i < count; i++) {
            days.add(1 + i * (lastDay - 1) / (count - 1));   // erster Einkauf am 1., letzter am lastDay
        }
        return days;
    }
}
