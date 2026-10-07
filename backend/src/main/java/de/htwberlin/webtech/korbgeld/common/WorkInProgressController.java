package de.htwberlin.webtech.korbgeld.common;

import de.htwberlin.webtech.korbgeld.common.error.FeatureNotAvailableException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Stubs für Bereiche, die noch in Arbeit sind. Jede Anfrage dorthin ergibt 501. */
@RestController
public class WorkInProgressController {

    @RequestMapping({"/api/recipes", "/api/recipes/**"})
    void recipes() {
        throw new FeatureNotAvailableException("Rezepte", "nach M4");
    }

    @RequestMapping({"/api/price-comparison", "/api/price-comparison/**"})
    void priceComparison() {
        throw new FeatureNotAvailableException("Preisvergleich", "nach M4");
    }

    @RequestMapping({"/api/profile", "/api/profile/**"})
    void profile() {
        throw new FeatureNotAvailableException("Profil", "M4 · 13. Dez.");
    }
}
