package de.htwberlin.webtech.korbgeld.budget;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface MonthlyBudgetRepository extends JpaRepository<MonthlyBudget, Long> {

    Optional<MonthlyBudget> findByOwnerIdAndYearMonth(Long ownerId, YearMonth yearMonth);

    boolean existsByOwnerIdAndYearMonth(Long ownerId, YearMonth yearMonth);

    List<MonthlyBudget> findAllByOwnerIdOrderByYearMonthAsc(Long ownerId);
}
