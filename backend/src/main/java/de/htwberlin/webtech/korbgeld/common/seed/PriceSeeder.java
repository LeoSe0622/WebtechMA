package de.htwberlin.webtech.korbgeld.common.seed;

import de.htwberlin.webtech.korbgeld.invest.PricePoint;
import de.htwberlin.webtech.korbgeld.invest.PricePointId;
import de.htwberlin.webtech.korbgeld.invest.PricePointRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/** Lädt die Monatskurse aus seed/prices.csv, wenn die Tabelle noch leer ist. */
@Component
public class PriceSeeder {

    private final PricePointRepository pricePointRepository;

    public PriceSeeder(PricePointRepository pricePointRepository) {
        this.pricePointRepository = pricePointRepository;
    }

    @Transactional
    public void seed() {
        if (pricePointRepository.count() > 0) {
            return;
        }
        List<PricePoint> points = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new ClassPathResource("seed/prices.csv").getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Kommentare (#) und die Kopfzeile überspringen
                if (line.isBlank() || line.startsWith("#") || line.startsWith("symbol")) {
                    continue;
                }
                String[] parts = line.split(",");
                points.add(new PricePoint(new PricePointId(parts[0], YearMonth.parse(parts[1])),
                        new BigDecimal(parts[2])));
            }
        } catch (IOException e) {
            throw new UncheckedIOException("seed/prices.csv konnte nicht gelesen werden", e);
        }
        pricePointRepository.saveAll(points);
    }
}
