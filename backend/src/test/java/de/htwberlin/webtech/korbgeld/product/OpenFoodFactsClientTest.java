package de.htwberlin.webtech.korbgeld.product;

import de.htwberlin.webtech.korbgeld.common.error.ExternalServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.net.SocketTimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/** MockRestServiceServer ersetzt Open Food Facts: Der Test ruft nie die echte API auf. */
class OpenFoodFactsClientTest {

    private static final String URL = "https://off.test/api/v2/product/4011800420413.json"
            + "?fields=product_name,product_name_de,image_front_small_url,nutriscore_grade,categories_tags";

    private MockRestServiceServer server;
    private OpenFoodFactsClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder()
                .baseUrl("https://off.test")
                .defaultHeader("User-Agent", "Korbgeld/0.1 (HTW Berlin Studienprojekt)");
        server = MockRestServiceServer.bindTo(builder).build();
        client = new OpenFoodFactsClient(builder.build());
    }

    @Test
    void returnsProductWithGermanNameAndSendsUserAgent() {
        server.expect(requestTo(URL))
                .andExpect(header("User-Agent", "Korbgeld/0.1 (HTW Berlin Studienprojekt)"))
                .andRespond(withSuccess("""
                        {"status":1,"product":{"product_name":"Oat drink","product_name_de":"Haferdrink",
                         "nutriscore_grade":"b","categories_tags":["en:beverages","en:plant-based-milks"]}}
                        """, MediaType.APPLICATION_JSON));

        var product = client.findByBarcode("4011800420413");

        assertThat(product).isPresent();
        assertThat(product.get().bestName()).isEqualTo("Haferdrink");
        assertThat(product.get().nutriScore()).isEqualTo("b");
        server.verify();
    }

    @Test
    void unknownBarcodeGivesEmptyResult() {
        server.expect(requestTo(URL)).andRespond(withStatus(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"status\":0,\"status_verbose\":\"product not found\"}"));

        assertThat(client.findByBarcode("4011800420413")).isEmpty();
    }

    @Test
    void timeoutBecomesExternalServiceException() {
        server.expect(requestTo(URL)).andRespond(withException(new SocketTimeoutException("Read timed out")));

        assertThatThrownBy(() -> client.findByBarcode("4011800420413"))
                .isInstanceOf(ExternalServiceException.class)
                .hasFieldOrPropertyWithValue("source", "Open Food Facts");
    }
}
