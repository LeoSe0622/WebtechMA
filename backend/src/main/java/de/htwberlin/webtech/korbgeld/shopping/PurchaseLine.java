package de.htwberlin.webtech.korbgeld.shopping;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/** Eine Position eines Einkaufs. Kein eigenes Entity, sondern Teil von Purchase (Tabelle purchase_line). */
@Embeddable
public class PurchaseLine {

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private int quantity;

    protected PurchaseLine() {
        // für JPA
    }

    public PurchaseLine(Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }
}
