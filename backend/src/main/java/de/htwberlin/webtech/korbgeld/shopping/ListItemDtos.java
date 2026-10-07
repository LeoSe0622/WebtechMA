package de.htwberlin.webtech.korbgeld.shopping;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public final class ListItemDtos {

    private ListItemDtos() {
    }

    /**
     * Neuer Eintrag: productId aus den Vorschlägen oder productName für ein neues Produkt.
     * force = true fügt trotz Vorrats-Warnung hinzu („Trotzdem hinzufügen“).
     */
    public record CreateListItemRequest(
            Long productId,
            @Size(max = 120) String productName,
            @NotNull @Min(value = 1, message = "muss zwischen 1 und 99 liegen")
            @Max(value = 99, message = "muss zwischen 1 und 99 liegen") Integer quantity,
            Boolean force
    ) {

        // Fehlt force im JSON, gilt es als false
        public boolean forced() {
            return Boolean.TRUE.equals(force);
        }
    }

    public record UpdateListItemRequest(
            Boolean checked,
            @Min(value = 1, message = "muss zwischen 1 und 99 liegen")
            @Max(value = 99, message = "muss zwischen 1 und 99 liegen") Integer quantity
    ) {
    }

    public record PantryHint(int quantity, LocalDate bestBefore) {
    }

    /** Entweder item (angelegt) oder alreadyInPantry (Warnung, nichts angelegt). */
    public record CreateListItemResponse(ListItemResponse item, PantryHint alreadyInPantry) {
    }
}
