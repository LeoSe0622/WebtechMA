package de.htwberlin.webtech.korbgeld.shopping;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ListItemRepository extends JpaRepository<ListItem, Long> {

    // Lädt das Produkt im selben SELECT mit (JOIN), statt pro Eintrag eine Extra-Abfrage zu schicken
    @EntityGraph(attributePaths = "product")
    List<ListItem> findAllByOwnerIdOrderByCreatedAtAsc(Long ownerId);

    @EntityGraph(attributePaths = "product")
    List<ListItem> findAllByOwnerIdAndCheckedTrue(Long ownerId);

    // Immer mit Besitzer suchen: Fremde Einträge gelten als "nicht gefunden"
    @EntityGraph(attributePaths = "product")
    Optional<ListItem> findByIdAndOwnerId(Long id, Long ownerId);
}
