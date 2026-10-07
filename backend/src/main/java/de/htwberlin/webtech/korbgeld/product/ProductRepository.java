package de.htwberlin.webtech.korbgeld.product;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Sichtbar für einen Nutzer sind gemeinsame Produkte (created_by IS NULL: Katalog, Open Food Facts)
 * und seine eigenen. Namen sind nicht eindeutig, deshalb immer "findFirst … OrderByIdAsc" (Review 02, B1).
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Seed-Katalog: gemeinsames, manuell angelegtes Produkt mit diesem Namen, das älteste gewinnt
    Optional<Product> findFirstBySourceAndCreatedByIsNullAndNameIgnoreCaseOrderByIdAsc(ProductSource source,
                                                                                       String name);

    Optional<Product> findFirstByCreatedByAndNameIgnoreCaseOrderByIdAsc(Long createdBy, String name);

    Optional<Product> findFirstByCreatedByIsNullAndNameIgnoreCaseOrderByIdAsc(String name);

    Optional<Product> findByBarcode(String barcode);

    @Query("select p from Product p where p.id = :id and (p.createdBy is null or p.createdBy = :userId)")
    Optional<Product> findVisible(Long id, Long userId);

    // Vorschläge beim Tippen: nur sichtbare Produkte; die Anzahl begrenzt der Pageable (10)
    @Query("select p from Product p where lower(p.name) like lower(concat('%', :query, '%')) "
            + "and (p.createdBy is null or p.createdBy = :userId) order by p.name")
    List<Product> searchVisible(String query, Long userId, Pageable pageable);
}
