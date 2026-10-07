package de.htwberlin.webtech.korbgeld.budget;

import de.htwberlin.webtech.korbgeld.auth.AppUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.math.BigDecimal;
import java.time.YearMonth;

@Entity
public class MonthlyBudget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private AppUser owner;

    // Gespeichert als "2026-10" (YearMonthConverter)
    @Column(name = "year_month", nullable = false, length = 7)
    private YearMonth yearMonth;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal amount;

    protected MonthlyBudget() {
        // für JPA
    }

    public MonthlyBudget(AppUser owner, YearMonth yearMonth, BigDecimal amount) {
        this.owner = owner;
        this.yearMonth = yearMonth;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public AppUser getOwner() {
        return owner;
    }

    public YearMonth getYearMonth() {
        return yearMonth;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void changeAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
