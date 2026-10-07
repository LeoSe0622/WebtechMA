package de.htwberlin.webtech.korbgeld.shopping;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public final class PurchaseDtos {

    private PurchaseDtos() {
    }

    /** Laden ist Pflicht: vorhandene storeId oder ein neuer storeName. */
    public record CompletePurchaseRequest(
            Long storeId,
            @Size(max = 60) String storeName,
            @NotNull
            @DecimalMin(value = "0.01", message = "muss zwischen 0,01 € und 1.000 € liegen")
            @DecimalMax(value = "1000.00", message = "muss zwischen 0,01 € und 1.000 € liegen")
            @Digits(integer = 4, fraction = 2) BigDecimal totalAmount
    ) {
    }

    public record PurchaseLineResponse(Long productId, int quantity) {
    }

    public record PurchaseResponse(Long id, Long storeId, String storeName, LocalDate date, BigDecimal totalAmount,
                                   List<PurchaseLineResponse> lines) {

        static PurchaseResponse from(Purchase purchase) {
            return new PurchaseResponse(purchase.getId(), purchase.getStore().getId(), purchase.getStore().getName(),
                    purchase.getDate(), purchase.getTotalAmount(),
                    purchase.getLines().stream()
                            .map(line -> new PurchaseLineResponse(line.getProductId(), line.getQuantity()))
                            .toList());
        }
    }

    /** remainingBudget ist null, wenn für den Monat kein Budget gesetzt ist. */
    public record CompletePurchaseResponse(PurchaseResponse purchase, BigDecimal remainingBudget) {
    }
}
