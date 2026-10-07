package de.htwberlin.webtech.korbgeld.invest;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PricePointRepository extends JpaRepository<PricePoint, PricePointId> {

    List<PricePoint> findAllByIdSymbolOrderByIdMonthAsc(String symbol);
}
