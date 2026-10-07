package de.htwberlin.webtech.korbgeld.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(unique = true, length = 32)
    private String barcode;

    @Column(length = 60)
    private String category;

    @Column(length = 500)
    private String imageUrl;

    @Column(length = 1)
    private String nutriScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductSource source;

    // Nutzer-ID bei selbst eingetippten Produkten (nur für ihn sichtbar); null bei Katalog und Open Food Facts
    @Column(name = "created_by")
    private Long createdBy;

    protected Product() {
        // für JPA
    }

    public Product(String name, ProductSource source) {
        this(name, null, null, null, null, source);
    }

    public Product(String name, String barcode, String category, String imageUrl, String nutriScore,
                   ProductSource source) {
        this.name = name;
        this.barcode = barcode;
        this.category = category;
        this.imageUrl = imageUrl;
        this.nutriScore = nutriScore;
        this.source = source;
    }

    /** Ein Produkt, das ein Nutzer selbst eingetippt hat. */
    public static Product ownedBy(Long userId, String name) {
        Product product = new Product(name, ProductSource.MANUAL);
        product.createdBy = userId;
        return product;
    }

    public Long getId() {
        return id;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public String getName() {
        return name;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getCategory() {
        return category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getNutriScore() {
        return nutriScore;
    }

    public ProductSource getSource() {
        return source;
    }
}
