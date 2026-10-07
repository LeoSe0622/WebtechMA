package de.htwberlin.webtech.korbgeld.common.seed;

import de.htwberlin.webtech.korbgeld.product.Product;
import de.htwberlin.webtech.korbgeld.product.ProductRepository;
import de.htwberlin.webtech.korbgeld.product.ProductSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Gemeinsamer Katalog mit typischen Supermarktprodukten, bewusst ohne Barcodes (keine erfundenen Barcodes). */
@Component
public class ProductCatalog {

    // Name → Kategorie
    static final Map<String, String> PRODUCTS = new LinkedHashMap<>();

    static {
        for (String name : List.of("Hafermilch", "Vollmilch", "Butter", "Naturjoghurt", "Gouda in Scheiben",
                "Eier (10 Stück)", "Quark")) {
            PRODUCTS.put(name, "Molkerei und Eier");
        }
        for (String name : List.of("Vollkornbrot", "Toastbrot", "Brötchen")) {
            PRODUCTS.put(name, "Backwaren");
        }
        for (String name : List.of("Haferflocken", "Müsli", "Spaghetti", "Penne", "Basmatireis", "Mehl", "Zucker")) {
            PRODUCTS.put(name, "Grundnahrungsmittel");
        }
        for (String name : List.of("Äpfel", "Bananen")) {
            PRODUCTS.put(name, "Obst");
        }
        for (String name : List.of("Kartoffeln", "Möhren", "Paprika rot", "Tomaten", "Gurke", "Zwiebeln", "Knoblauch")) {
            PRODUCTS.put(name, "Gemüse");
        }
        for (String name : List.of("Tomaten passiert", "Kidneybohnen", "Kichererbsen")) {
            PRODUCTS.put(name, "Konserven");
        }
        for (String name : List.of("Tofu", "Hähnchenbrust", "Lachsfilet")) {
            PRODUCTS.put(name, "Fleisch, Fisch und Tofu");
        }
        for (String name : List.of("Kaffee gemahlen", "Schwarzer Tee", "Mineralwasser", "Orangensaft")) {
            PRODUCTS.put(name, "Getränke");
        }
        for (String name : List.of("Erdnussbutter", "Marmelade", "Honig", "Olivenöl")) {
            PRODUCTS.put(name, "Aufstriche und Öle");
        }
    }

    private final ProductRepository productRepository;

    public ProductCatalog(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /** Legt fehlende Katalogprodukte an. Mehrfach aufrufbar, ohne Duplikate (idempotent). */
    @Transactional
    public void ensureCatalog() {
        PRODUCTS.forEach((name, category) -> {
            if (productRepository.findByNameIgnoreCase(name).isEmpty()) {
                productRepository.save(new Product(name, null, category, null, null, ProductSource.MANUAL));
            }
        });
    }

    public Product get(String name) {
        return productRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new IllegalStateException("Katalogprodukt fehlt: " + name));
    }

    public List<String> names() {
        return List.copyOf(PRODUCTS.keySet());
    }
}
