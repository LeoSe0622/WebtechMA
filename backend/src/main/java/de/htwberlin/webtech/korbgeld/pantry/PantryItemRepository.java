package de.htwberlin.webtech.korbgeld.pantry;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PantryItemRepository extends JpaRepository<PantryItem, Long> {

    @EntityGraph(attributePaths = "product")
    List<PantryItem> findAllByOwnerId(Long ownerId);

    @EntityGraph(attributePaths = "product")
    Optional<PantryItem> findByIdAndOwnerId(Long id, Long ownerId);

    // Für die Vorrats-Warnung und das Zusammenfassen beim Einkauf
    List<PantryItem> findAllByOwnerIdAndProductId(Long ownerId, Long productId);
}
