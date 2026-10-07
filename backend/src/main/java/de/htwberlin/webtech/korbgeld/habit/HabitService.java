package de.htwberlin.webtech.korbgeld.habit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class HabitService {

    public record HabitResponse(Long id, Long productId, String productName, int quantity, int intervalDays,
                                LocalDate nextDue) {

        static HabitResponse from(Habit habit) {
            return new HabitResponse(habit.getId(), habit.getProduct().getId(), habit.getProduct().getName(),
                    habit.getQuantity(), habit.getIntervalDays(), habit.getNextDue());
        }
    }

    private final HabitRepository habitRepository;

    public HabitService(HabitRepository habitRepository) {
        this.habitRepository = habitRepository;
    }

    @Transactional(readOnly = true)
    public List<HabitResponse> findAll(Long userId) {
        return habitRepository.findAllByOwnerIdOrderByNextDueAsc(userId).stream()
                .map(HabitResponse::from)
                .toList();
    }
}
