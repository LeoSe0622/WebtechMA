package de.htwberlin.webtech.korbgeld.shopping;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListItemRepository extends JpaRepository<ListItem, Long> {

    // Lädt das Produkt im selben SELECT mit (JOIN), statt pro Eintrag eine Extra-Abfrage zu schicken
    @EntityGraph(attributePaths = "product")
    List<ListItem> findAllByOrderByCreatedAtAsc();
}
