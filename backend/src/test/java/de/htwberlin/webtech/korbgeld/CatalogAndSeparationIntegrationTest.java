package de.htwberlin.webtech.korbgeld;

import com.jayway.jsonpath.JsonPath;
import de.htwberlin.webtech.korbgeld.product.Product;
import de.htwberlin.webtech.korbgeld.product.ProductRepository;
import de.htwberlin.webtech.korbgeld.product.ProductSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Regressionstests zu Review 02: gemeinsamer Produktkatalog (B1, M3) und Datentrennung über die Liste hinaus (M4). */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@Testcontainers(disabledWithoutDocker = true)
class CatalogAndSeparationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void scannedProductWithACatalogNameDoesNotBreakDemoLogin() throws Exception {
        // Wie nach einem Scan: Open-Food-Facts-Produkt mit demselben Namen wie ein Katalogprodukt
        String barcode = String.valueOf(ThreadLocalRandom.current().nextLong(1_000_000_000_000L, 9_999_999_999_999L));
        productRepository.save(new Product("Butter", barcode, null, null, null, ProductSource.OPEN_FOOD_FACTS));

        String token = demoToken();
        mockMvc.perform(post("/api/list-items").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"productName\":\"Butter\",\"quantity\":1}"))
                .andExpect(status().isCreated());
    }

    @Test
    void ownTypedProductsAreInvisibleToOtherUsers() throws Exception {
        String tokenA = demoToken();
        String tokenB = demoToken();
        String name = "Geheimprodukt-" + UUID.randomUUID().toString().substring(0, 8);

        String created = mockMvc.perform(post("/api/list-items").header("Authorization", bearer(tokenA))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"productName\":\"%s\",\"quantity\":1}".formatted(name)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        Integer productId = JsonPath.read(created, "$.item.productId");

        // A findet sein Produkt, B nicht
        mockMvc.perform(get("/api/products").param("query", name).header("Authorization", bearer(tokenA)))
                .andExpect(jsonPath("$.length()").value(1));
        mockMvc.perform(get("/api/products").param("query", name).header("Authorization", bearer(tokenB)))
                .andExpect(jsonPath("$.length()").value(0));

        // B kann es auch nicht über die ID verwenden
        mockMvc.perform(post("/api/list-items").header("Authorization", bearer(tokenB))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"productId\":%d,\"quantity\":1}".formatted(productId)))
                .andExpect(status().isNotFound());
    }

    @Test
    void storesAndPantryOfOtherUsersAreNotFound() throws Exception {
        String tokenA = demoToken();
        String tokenB = demoToken();
        Integer storeOfA = JsonPath.read(body(get("/api/stores"), tokenA), "$[0].id");
        Integer pantryOfA = JsonPath.read(body(get("/api/pantry-items"), tokenA), "$[0].id");

        // B hakt einen eigenen Eintrag ab und versucht, mit A's Laden einzukaufen
        Integer itemOfB = JsonPath.read(body(get("/api/list-items"), tokenB), "$[0].id");
        mockMvc.perform(patch("/api/list-items/" + itemOfB).header("Authorization", bearer(tokenB))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"checked\":true}"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/purchases").header("Authorization", bearer(tokenB))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"storeId\":%d,\"totalAmount\":5.00}".formatted(storeOfA)))
                .andExpect(status().isNotFound());

        // B kann A's Vorrat weder ändern noch verbrauchen
        mockMvc.perform(patch("/api/pantry-items/" + pantryOfA).header("Authorization", bearer(tokenB))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"quantity\":9}"))
                .andExpect(status().isNotFound());
        mockMvc.perform(post("/api/pantry-items/" + pantryOfA + "/consume").header("Authorization", bearer(tokenB)))
                .andExpect(status().isNotFound());
    }

    private String body(org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder request,
                        String token) throws Exception {
        return mockMvc.perform(request.header("Authorization", bearer(token)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
    }

    private String demoToken() throws Exception {
        String response = mockMvc.perform(post("/api/auth/demo"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return JsonPath.read(response, "$.token");
    }

    private static String bearer(String token) {
        return "Bearer " + token;
    }
}
