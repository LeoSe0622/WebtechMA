package de.htwberlin.webtech.korbgeld.pantry;

import de.htwberlin.webtech.korbgeld.auth.AppUser;
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
import java.time.LocalDate;

@Entity
public class PantryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private AppUser owner;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    // Mindesthaltbarkeit, optional
    private LocalDate bestBefore;

    @Column(nullable = false)
    private Instant addedAt;

    protected PantryItem() {
        // für JPA
    }

    public PantryItem(AppUser owner, Product product, int quantity, LocalDate bestBefore, Instant addedAt) {
        this.owner = owner;
        this.product = product;
        this.quantity = quantity;
        this.bestBefore = bestBefore;
        this.addedAt = addedAt;
    }

    public Long getId() {
        return id;
    }

    public AppUser getOwner() {
        return owner;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDate getBestBefore() {
        return bestBefore;
    }

    public Instant getAddedAt() {
        return addedAt;
    }

    public void increaseQuantity(int amount) {
        this.quantity += amount;
    }

    public void changeQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void changeBestBefore(LocalDate bestBefore) {
        this.bestBefore = bestBefore;
    }
}
