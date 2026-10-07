package de.htwberlin.webtech.korbgeld.invest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Sparplan (UC7), vorerst nur die Musterportfolios (Demo). Risikoprofil, Rechner und Diagramm folgen zu M4.
 * Ohne Service: Es gibt keine Datenbankabfrage und keine Logik, nur die festen Werte aus dem Enum (E20).
 */
@RestController
@RequestMapping("/api/invest")
public class InvestController {

    private static final Map<String, String> SYMBOL_NAMES = Map.of(
            "ACWI", "Aktien weltweit (ETF, US-gelistet)",
            "AGG", "Anleihen (ETF, US-gelistet)");

    public record WeightResponse(String symbol, String name, BigDecimal share) {
    }

    public record PortfolioResponse(String id, String label, List<WeightResponse> weights) {
    }

    @GetMapping("/portfolios")
    public List<PortfolioResponse> portfolios() {
        return Arrays.stream(ModelPortfolio.values())
                .map(portfolio -> new PortfolioResponse(portfolio.name(), portfolio.label(),
                        portfolio.weights().stream()
                                .map(w -> new WeightResponse(w.symbol(), SYMBOL_NAMES.get(w.symbol()), w.share()))
                                .toList()))
                .toList();
    }
}
