package de.htwberlin.webtech.korbgeld;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Die ganze Kern-Kette über HTTP gegen eine echte Postgres (AUFTRAG.md, Abschnitt 13):
 * Demo-Login → Artikel auf die Liste → abhaken → Einkauf abschließen → Vorrat → Restbudget.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@Testcontainers(disabledWithoutDocker = true)
class CoreChainIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void fullChainFromListToPantryLowersRemainingBudgetByTheTotal() throws Exception {
        String token = demoToken();
        BigDecimal remainingBefore = remaining(token);
        String productName = "Testkekse " + UUID.randomUUID().toString().substring(0, 6);

        // 1. Neues Produkt auf die Liste setzen
        String created = mockMvc.perform(post("/api/list-items").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productName\":\"%s\",\"quantity\":2}".formatted(productName)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.item.productName").value(productName))
                .andReturn().getResponse().getContentAsString();
        Integer itemId = JsonPath.read(created, "$.item.id");

        // 2. Abhaken
        mockMvc.perform(patch("/api/list-items/" + itemId).header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"checked\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.checked").value(true));

        // 3. Einkauf mit neuem Laden und Summe abschließen
        mockMvc.perform(post("/api/purchases").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"storeName\":\"Testmarkt\",\"totalAmount\":12.34}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.purchase.storeName").value("Testmarkt"))
                .andExpect(jsonPath("$.remainingBudget").value(remainingBefore.subtract(new BigDecimal("12.34")).doubleValue()));

        // 4. Der Artikel steht im Vorrat und ist nicht mehr auf der Liste
        String pantry = mockMvc.perform(get("/api/pantry-items").header("Authorization", bearer(token)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        List<String> pantryNames = JsonPath.read(pantry, "$[*].productName");
        assertThat(pantryNames).contains(productName);

        String list = mockMvc.perform(get("/api/list-items").header("Authorization", bearer(token)))
                .andReturn().getResponse().getContentAsString();
        List<Integer> listIds = JsonPath.read(list, "$[*].id");
        assertThat(listIds).doesNotContain(itemId);

        // 5. Restbudget genau um die Summe gesunken, Budget jetzt gesperrt
        assertThat(remaining(token)).isEqualByComparingTo(remainingBefore.subtract(new BigDecimal("12.34")));
        mockMvc.perform(get("/api/budgets/current/summary").header("Authorization", bearer(token)))
                .andExpect(jsonPath("$.locked").value(true));
    }

    @Test
    void productAlreadyInPantryTriggersWarningUntilForced() throws Exception {
        String token = demoToken();
        // Persona „Mia“ hat Basmatireis im Vorrat
        String product = mockMvc.perform(get("/api/products").param("query", "Basmatireis")
                        .header("Authorization", bearer(token)))
                .andReturn().getResponse().getContentAsString();
        Integer productId = JsonPath.read(product, "$[0].id");

        mockMvc.perform(post("/api/list-items").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":%d,\"quantity\":1}".formatted(productId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item").doesNotExist())
                .andExpect(jsonPath("$.alreadyInPantry.quantity").value(1));

        mockMvc.perform(post("/api/list-items").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":%d,\"quantity\":1,\"force\":true}".formatted(productId)))
                .andExpect(status().isCreated());
    }

    @Test
    void userBCanNeitherReadNorChangeItemsOfUserA() throws Exception {
        String tokenA = demoToken();
        String tokenB = demoToken();
        String listA = mockMvc.perform(get("/api/list-items").header("Authorization", bearer(tokenA)))
                .andReturn().getResponse().getContentAsString();
        Integer itemOfA = JsonPath.read(listA, "$[0].id");

        // B sieht A's Eintrag nicht in seiner Liste …
        String listB = mockMvc.perform(get("/api/list-items").header("Authorization", bearer(tokenB)))
                .andReturn().getResponse().getContentAsString();
        List<Integer> idsOfB = JsonPath.read(listB, "$[*].id");
        assertThat(idsOfB).doesNotContain(itemOfA);

        // … und kann ihn weder ändern noch löschen: für B gibt es ihn nicht
        mockMvc.perform(patch("/api/list-items/" + itemOfA).header("Authorization", bearer(tokenB))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"checked\":true}"))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/list-items/" + itemOfA).header("Authorization", bearer(tokenB)))
                .andExpect(status().isNotFound());

        // A's Eintrag ist unverändert
        mockMvc.perform(get("/api/list-items").header("Authorization", bearer(tokenA)))
                .andExpect(jsonPath("$[0].id").value(itemOfA))
                .andExpect(jsonPath("$[0].checked").value(false));
    }

    private BigDecimal remaining(String token) throws Exception {
        String summary = mockMvc.perform(get("/api/budgets/current/summary").header("Authorization", bearer(token)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return new BigDecimal(JsonPath.read(summary, "$.remaining").toString());
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
