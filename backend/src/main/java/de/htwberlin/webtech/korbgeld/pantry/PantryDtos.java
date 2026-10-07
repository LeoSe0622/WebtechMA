package de.htwberlin.webtech.korbgeld.pantry;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;

public final class PantryDtos {

    private PantryDtos() {
    }

    /** Ablauf-Status (AUFTRAG.md, Abschnitt 7): ≤ 3 Tage „läuft bald ab“, Vergangenheit „abgelaufen“. */
    public enum ExpiryStatus {
        EXPIRED, EXPIRING_SOON, OK, NO_DATE
    }

    public record PantryItemResponse(Long id, Long productId, String productName, int quantity,
                                     LocalDate bestBefore, ExpiryStatus expiryStatus) {
    }

    public record UpdatePantryItemRequest(@Min(1) @Max(999) Integer quantity, LocalDate bestBefore,
                                          Boolean clearBestBefore) {
    }

    public record ConsumeRequest(@Min(1) @Max(999) Integer amount) {
    }
}
