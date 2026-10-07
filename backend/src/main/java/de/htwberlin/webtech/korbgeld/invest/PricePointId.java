package de.htwberlin.webtech.korbgeld.invest;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.YearMonth;
import java.util.Objects;

/** Zusammengesetzter Primärschlüssel (symbol, month) für PricePoint. */
@Embeddable
public class PricePointId {

    @Column(nullable = false, length = 10)
    private String symbol;

    @Column(nullable = false, length = 7)
    private YearMonth month;

    protected PricePointId() {
        // für JPA
    }

    public PricePointId(String symbol, YearMonth month) {
        this.symbol = symbol;
        this.month = month;
    }

    public String getSymbol() {
        return symbol;
    }

    public YearMonth getMonth() {
        return month;
    }

    // Ein Schlüssel braucht equals und hashCode, damit Hibernate gleiche Schlüssel erkennt
    @Override
    public boolean equals(Object other) {
        return other instanceof PricePointId that && symbol.equals(that.symbol) && month.equals(that.month);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, month);
    }
}
