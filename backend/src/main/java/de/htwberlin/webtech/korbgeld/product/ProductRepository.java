package de.htwberlin.webtech.korbgeld.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByNameIgnoreCase(String name);

    Optional<Product> findByBarcode(String barcode);

    // Vorschläge beim Tippen: höchstens 10 Treffer
    List<Product> findTop10ByNameContainingIgnoreCaseOrderByNameAsc(String query);
}
