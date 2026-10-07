package de.htwberlin.webtech.korbgeld.habit;

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

import java.time.LocalDate;

@Entity
public class Habit {

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

    @Column(nullable = false)
    private int intervalDays;

    @Column(nullable = false)
    private LocalDate nextDue;

    protected Habit() {
        // für JPA
    }

    public Habit(AppUser owner, Product product, int quantity, int intervalDays, LocalDate nextDue) {
        this.owner = owner;
        this.product = product;
        this.quantity = quantity;
        this.intervalDays = intervalDays;
        this.nextDue = nextDue;
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

    public int getIntervalDays() {
        return intervalDays;
    }

    public LocalDate getNextDue() {
        return nextDue;
    }
}
