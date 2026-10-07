package de.htwberlin.webtech.korbgeld.shopping;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    @EntityGraph(attributePaths = "store")
    List<Purchase> findAllByOwnerIdAndDateBetweenOrderByDateDesc(Long ownerId, LocalDate from, LocalDate to);

    boolean existsByOwnerIdAndDateBetween(Long ownerId, LocalDate from, LocalDate to);

    // Summe aller Einkäufe im Zeitraum; coalesce liefert 0 statt NULL, wenn es keine gibt
    @Query("select coalesce(sum(p.totalAmount), 0) from Purchase p "
            + "where p.owner.id = :ownerId and p.date between :from and :to")
    BigDecimal sumTotalAmount(Long ownerId, LocalDate from, LocalDate to);
}
