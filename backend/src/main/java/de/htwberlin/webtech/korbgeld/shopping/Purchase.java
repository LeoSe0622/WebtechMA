package de.htwberlin.webtech.korbgeld.shopping;

import de.htwberlin.webtech.korbgeld.auth.AppUser;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private AppUser owner;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "purchase_date", nullable = false)
    private LocalDate date;

    // Geld immer als BigDecimal, nie double (Rundungsfehler)
    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal totalAmount;

    @ElementCollection
    @CollectionTable(name = "purchase_line", joinColumns = @JoinColumn(name = "purchase_id"))
    private List<PurchaseLine> lines = new ArrayList<>();

    protected Purchase() {
        // für JPA
    }

    public Purchase(AppUser owner, Store store, LocalDate date, BigDecimal totalAmount, List<PurchaseLine> lines) {
        this.owner = owner;
        this.store = store;
        this.date = date;
        this.totalAmount = totalAmount;
        this.lines = new ArrayList<>(lines);
    }

    public Long getId() {
        return id;
    }

    public AppUser getOwner() {
        return owner;
    }

    public Store getStore() {
        return store;
    }

    public LocalDate getDate() {
        return date;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public List<PurchaseLine> getLines() {
        return lines;
    }
}
