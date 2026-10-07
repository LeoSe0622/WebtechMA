package de.htwberlin.webtech.korbgeld.product;

import de.htwberlin.webtech.korbgeld.common.error.BadRequestException;
import de.htwberlin.webtech.korbgeld.common.error.NotFoundException;
import de.htwberlin.webtech.korbgeld.product.OpenFoodFactsClient.OffProduct;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final OpenFoodFactsClient openFoodFactsClient;

    public ProductService(ProductRepository productRepository, OpenFoodFactsClient openFoodFactsClient) {
        this.productRepository = productRepository;
        this.openFoodFactsClient = openFoodFactsClient;
    }

    /** Vorschläge: gemeinsame Produkte und die eigenen, höchstens 10. */
    @Transactional(readOnly = true)
    public List<ProductResponse> search(Long userId, String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        return productRepository.searchVisible(query.trim(), userId, PageRequest.of(0, 10)).stream()
                .map(ProductResponse::from)
                .toList();
    }

    /**
     * Erst in der eigenen Datenbank (Cache), dann bei Open Food Facts; neue Treffer werden gespeichert.
     * Bewusst ohne @Transactional: Während des externen Aufrufs (bis 3 s) soll keine Datenbankverbindung
     * aus dem kleinen Pool blockiert sein. Jeder Repository-Aufruf hat seine eigene kurze Transaktion.
     */
    public ProductResponse findByBarcode(String barcode) {
        if (!barcode.matches("\\d{8,14}")) {
            throw new BadRequestException("Ein Barcode besteht aus 8 bis 14 Ziffern.");
        }
        return productRepository.findByBarcode(barcode)
                .map(ProductResponse::from)
                .orElseGet(() -> ProductResponse.from(productRepository.save(fetchFromOpenFoodFacts(barcode))));
    }

    private Product fetchFromOpenFoodFacts(String barcode) {
        OffProduct off = openFoodFactsClient.findByBarcode(barcode)
                .filter(p -> p.bestName() != null && !p.bestName().isBlank())
                .orElseThrow(() -> new NotFoundException("Zu diesem Barcode kennen wir kein Produkt."));
        String name = off.bestName().length() > 120 ? off.bestName().substring(0, 120) : off.bestName();
        String category = categoryOf(off.categoriesTags());
        String nutriScore = off.nutriScore() != null && off.nutriScore().length() == 1
                ? off.nutriScore().toUpperCase() : null;
        // Überlange Bild-URLs passen nicht in die Spalte (500 Zeichen) und werden weggelassen
        String imageUrl = off.imageUrl() != null && off.imageUrl().length() <= 500 ? off.imageUrl() : null;
        return new Product(name, barcode, category, imageUrl, nutriScore, ProductSource.OPEN_FOOD_FACTS);
    }

    // Aus "en:plant-based-milks" wird "plant based milks"
    private static String categoryOf(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return null;
        }
        String tag = tags.get(tags.size() - 1);
        String readable = tag.substring(tag.indexOf(':') + 1).replace('-', ' ');
        return readable.length() > 60 ? readable.substring(0, 60) : readable;
    }
}
