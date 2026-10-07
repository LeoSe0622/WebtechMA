package de.htwberlin.webtech.korbgeld.habit;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HabitRepository extends JpaRepository<Habit, Long> {

    @EntityGraph(attributePaths = "product")
    List<Habit> findAllByOwnerIdOrderByNextDueAsc(Long ownerId);
}
