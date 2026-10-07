package de.htwberlin.webtech.korbgeld.shopping;

import java.time.Instant;

public record ListItemResponse(
        Long id,
        Long productId,
        String productName,
        int quantity,
        boolean checked,
        Instant createdAt
) {

    static ListItemResponse from(ListItem item) {
        return new ListItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getChecked(),
                item.getCreatedAt()
        );
    }
}
