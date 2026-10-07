package de.htwberlin.webtech.korbgeld.invest;

import java.math.BigDecimal;
import java.util.List;

/**
 * Musterportfolios (AUFTRAG.md, Abschnitt 7). Bewusst ein Enum im Code, keine Tabelle: Sie ändern sich nicht
 * und dienen nur zum Lernen, nicht als Anlageempfehlung.
 */
public enum ModelPortfolio {

    VORSICHTIG("Vorsichtig", List.of(new Weight("ACWI", "0.30"), new Weight("AGG", "0.70"))),
    AUSGEWOGEN("Ausgewogen", List.of(new Weight("ACWI", "0.60"), new Weight("AGG", "0.40"))),
    MUTIG("Mutig", List.of(new Weight("ACWI", "1.00")));

    public record Weight(String symbol, BigDecimal share) {

        Weight(String symbol, String share) {
            this(symbol, new BigDecimal(share));
        }
    }

    private final String label;
    private final List<Weight> weights;

    ModelPortfolio(String label, List<Weight> weights) {
        this.label = label;
        this.weights = weights;
    }

    public String label() {
        return label;
    }

    public List<Weight> weights() {
        return weights;
    }
}
