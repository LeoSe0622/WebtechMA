package de.htwberlin.webtech.korbgeld.product;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Der Produktkatalog ist gemeinsam für alle Nutzer, deshalb ohne Besitzer. */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Vorschläge beim Tippen: GET /api/products?query=milch
    @GetMapping
    public List<ProductResponse> search(@RequestParam(defaultValue = "") String query) {
        return productService.search(query);
    }

    @GetMapping("/barcode/{code}")
    public ProductResponse byBarcode(@PathVariable String code) {
        return productService.findByBarcode(code);
    }
}
