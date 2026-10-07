package de.htwberlin.webtech.korbgeld.product;

public record ProductResponse(Long id, String name, String barcode, String category, String imageUrl,
                              String nutriScore, ProductSource source) {

    public static ProductResponse from(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getBarcode(), product.getCategory(),
                product.getImageUrl(), product.getNutriScore(), product.getSource());
    }
}
