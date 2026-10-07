package de.htwberlin.webtech.korbgeld.product;

import de.htwberlin.webtech.korbgeld.common.error.NotFoundException;
import de.htwberlin.webtech.korbgeld.product.OpenFoodFactsClient.OffProduct;
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

    @Transactional(readOnly = true)
    public List<ProductResponse> search(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        return productRepository.findTop10ByNameContainingIgnoreCaseOrderByNameAsc(query.trim()).stream()
                .map(ProductResponse::from)
                .toList();
    }

    /** Erst in der eigenen Datenbank (Cache), dann bei Open Food Facts; neue Treffer werden gespeichert. */
    @Transactional
    public ProductResponse findByBarcode(String barcode) {
        if (!barcode.matches("\\d{8,14}")) {
            throw new NotFoundException("Das ist kein gültiger Barcode.");
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
        return new Product(name, barcode, category, off.imageUrl(), nutriScore, ProductSource.OPEN_FOOD_FACTS);
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
