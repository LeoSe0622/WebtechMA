package de.htwberlin.webtech.korbgeld.shopping;

import de.htwberlin.webtech.korbgeld.product.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.Instant;

@Entity
public class ListItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // LAZY: Produkt erst laden, wenn es gebraucht wird; das Repository holt es per @EntityGraph mit
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private boolean checked;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected ListItem() {
        // für JPA
    }

    // createdAt als Parameter, damit Service (Clock) und Seeder (vergangene Monate) ihn bestimmen
    public ListItem(Product product, int quantity, Instant createdAt) {
        this.product = product;
        this.quantity = quantity;
        this.checked = false;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean isChecked() {
        return checked;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
