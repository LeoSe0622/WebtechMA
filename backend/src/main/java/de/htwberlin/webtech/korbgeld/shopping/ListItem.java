package de.htwberlin.webtech.korbgeld.shopping;

import de.htwberlin.webtech.korbgeld.product.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Boolean checked;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected ListItem() {
        // für JPA
    }

    public ListItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.checked = false;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Boolean getChecked() {
        return checked;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
