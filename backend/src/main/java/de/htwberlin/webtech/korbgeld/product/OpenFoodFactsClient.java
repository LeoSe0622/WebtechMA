package de.htwberlin.webtech.korbgeld.product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.htwberlin.webtech.korbgeld.common.error.ExternalServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Optional;

/**
 * Fragt Open Food Facts nach einem Barcode. Ohne Konto und ohne Key, aber mit eigenem User-Agent
 * (Bitte der Betreiber) und 3 Sekunden Timeout (OpenFoodFactsConfig).
 */
@Component
public class OpenFoodFactsClient {

    static final String SOURCE = "Open Food Facts";
    private static final String FIELDS = "product_name,product_name_de,image_front_small_url,nutriscore_grade,categories_tags";

    private final RestClient restClient;

    public OpenFoodFactsClient(RestClient openFoodFactsRestClient) {
        this.restClient = openFoodFactsRestClient;
    }

    /** Leer, wenn Open Food Facts den Barcode nicht kennt. ExternalServiceException, wenn der Dienst ausfällt. */
    public Optional<OffProduct> findByBarcode(String barcode) {
        try {
            OffResponse response = restClient.get()
                    .uri("/api/v2/product/{barcode}.json?fields=" + FIELDS, barcode)
                    .retrieve()
                    .body(OffResponse.class);
            if (response == null || response.status() != 1 || response.product() == null) {
                return Optional.empty();
            }
            return Optional.of(response.product());
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();   // unbekannter Barcode
            }
            throw new ExternalServiceException(SOURCE, "Open Food Facts hat die Anfrage abgelehnt.", e);
        } catch (RestClientException e) {
            // Timeout, keine Verbindung, Serverfehler
            throw new ExternalServiceException(SOURCE, "Open Food Facts ist gerade nicht erreichbar.", e);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record OffResponse(int status, OffProduct product) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record OffProduct(
            @JsonProperty("product_name") String productName,
            @JsonProperty("product_name_de") String productNameDe,
            @JsonProperty("image_front_small_url") String imageUrl,
            @JsonProperty("nutriscore_grade") String nutriScore,
            @JsonProperty("categories_tags") List<String> categoriesTags
    ) {

        /** Deutscher Name, sonst der allgemeine Name. */
        public String bestName() {
            if (productNameDe != null && !productNameDe.isBlank()) {
                return productNameDe;
            }
            return productName;
        }
    }
}
