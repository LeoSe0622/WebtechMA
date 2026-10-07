package de.htwberlin.webtech.korbgeld.product;

import de.htwberlin.webtech.korbgeld.common.CurrentUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Katalog- und Open-Food-Facts-Produkte sind gemeinsam, selbst eingetippte nur für ihren Ersteller sichtbar. */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Vorschläge beim Tippen: GET /api/products?query=milch
    @GetMapping
    public List<ProductResponse> search(@AuthenticationPrincipal Jwt jwt,
                                        @RequestParam(defaultValue = "") String query) {
        return productService.search(CurrentUser.id(jwt), query);
    }

    @GetMapping("/barcode/{code}")
    public ProductResponse byBarcode(@PathVariable String code) {
        return productService.findByBarcode(code);
    }
}
