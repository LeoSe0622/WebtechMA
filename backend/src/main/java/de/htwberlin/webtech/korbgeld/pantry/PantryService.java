package de.htwberlin.webtech.korbgeld.pantry;

import de.htwberlin.webtech.korbgeld.common.error.NotFoundException;
import de.htwberlin.webtech.korbgeld.pantry.PantryDtos.ExpiryStatus;
import de.htwberlin.webtech.korbgeld.pantry.PantryDtos.PantryItemResponse;
import de.htwberlin.webtech.korbgeld.pantry.PantryDtos.UpdatePantryItemRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class PantryService {

    static final int SOON_DAYS = 3;

    private final PantryItemRepository pantryItemRepository;
    private final Clock clock;

    public PantryService(PantryItemRepository pantryItemRepository, Clock clock) {
        this.pantryItemRepository = pantryItemRepository;
        this.clock = clock;
    }

    /** Sortiert nach Mindesthaltbarkeit, das Dringendste zuerst; Einträge ohne Datum am Ende. */
    @Transactional(readOnly = true)
    public List<PantryItemResponse> findAll(Long userId) {
        LocalDate today = LocalDate.now(clock);
        return pantryItemRepository.findAllByOwnerId(userId).stream()
                .sorted(Comparator.comparing(PantryItem::getBestBefore, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(item -> item.getProduct().getName()))
                .map(item -> toResponse(item, today))
                .toList();
    }

    @Transactional
    public PantryItemResponse update(Long userId, Long id, UpdatePantryItemRequest request) {
        PantryItem item = findOwn(userId, id);
        if (request.quantity() != null) {
            item.changeQuantity(request.quantity());
        }
        if (Boolean.TRUE.equals(request.clearBestBefore())) {
            item.changeBestBefore(null);
        } else if (request.bestBefore() != null) {
            item.changeBestBefore(request.bestBefore());
        }
        return toResponse(item, LocalDate.now(clock));
    }

    /** Verbrauchen verringert die Menge; bei 0 wird der Eintrag gelöscht (dann leeres Ergebnis). */
    @Transactional
    public Optional<PantryItemResponse> consume(Long userId, Long id, int amount) {
        PantryItem item = findOwn(userId, id);
        int left = item.getQuantity() - amount;
        if (left <= 0) {
            pantryItemRepository.delete(item);
            return Optional.empty();
        }
        item.changeQuantity(left);
        return Optional.of(toResponse(item, LocalDate.now(clock)));
    }

    static ExpiryStatus expiryStatus(LocalDate bestBefore, LocalDate today) {
        if (bestBefore == null) {
            return ExpiryStatus.NO_DATE;
        }
        if (bestBefore.isBefore(today)) {
            return ExpiryStatus.EXPIRED;
        }
        if (!bestBefore.isAfter(today.plusDays(SOON_DAYS))) {
            return ExpiryStatus.EXPIRING_SOON;
        }
        return ExpiryStatus.OK;
    }

    private PantryItem findOwn(Long userId, Long id) {
        return pantryItemRepository.findByIdAndOwnerId(id, userId)
                .orElseThrow(() -> new NotFoundException("Diesen Vorratseintrag gibt es nicht."));
    }

    private static PantryItemResponse toResponse(PantryItem item, LocalDate today) {
        return new PantryItemResponse(item.getId(), item.getProduct().getId(), item.getProduct().getName(),
                item.getQuantity(), item.getBestBefore(), expiryStatus(item.getBestBefore(), today));
    }
}
