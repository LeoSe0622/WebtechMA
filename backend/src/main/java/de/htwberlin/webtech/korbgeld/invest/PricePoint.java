package de.htwberlin.webtech.korbgeld.invest;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;

import java.math.BigDecimal;

/** Monatlicher Schlusskurs eines ETFs. */
@Entity
public class PricePoint {

    @EmbeddedId
    private PricePointId id;

    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal close;

    protected PricePoint() {
        // für JPA
    }

    public PricePoint(PricePointId id, BigDecimal close) {
        this.id = id;
        this.close = close;
    }

    public PricePointId getId() {
        return id;
    }

    public BigDecimal getClose() {
        return close;
    }
}
