package de.htwberlin.webtech.korbgeld.auth;

import com.jayway.jsonpath.JsonPath;
import de.htwberlin.webtech.korbgeld.TestcontainersConfiguration;
import de.htwberlin.webtech.korbgeld.common.ClockConfig;
import de.htwberlin.webtech.korbgeld.common.seed.DemoPersona;
import de.htwberlin.webtech.korbgeld.common.seed.ProductCatalog;
import de.htwberlin.webtech.korbgeld.product.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Clock;
import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Review 03, M3: Das Löschen alter Sandbox-Nutzer hängt an einer Kette von ON DELETE CASCADE.
 * Kommt zu M4 eine Tabelle ohne Cascade dazu, scheitert dieser Test, nicht erst die Produktion.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@Testcontainers(disabledWithoutDocker = true)
class SandboxCleanupIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductCatalog productCatalog;
    @Autowired
    private DemoPersona demoPersona;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    void expiredSandboxUserIsDeletedWithAllOwnDataAndDemoLoginStillWorks() throws Exception {
        // Demo-Nutzer mit eigenem Produkt, Einkauf, Vorrat, Liste und Gewohnheiten (aus der Persona)
        String token = demoToken();
        String productName = "Eigenes Testprodukt " + UUID.randomUUID().toString().substring(0, 6);
        String created = mockMvc.perform(post("/api/list-items").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productName\":\"%s\",\"quantity\":1}".formatted(productName)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        Integer itemId = JsonPath.read(created, "$.item.id");
        Integer ownProductId = JsonPath.read(created, "$.item.productId");
        mockMvc.perform(patch("/api/list-items/" + itemId).header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"checked\":true}"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/purchases").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"storeName\":\"Testladen\",\"totalAmount\":3.50}"))
                .andExpect(status().isCreated());
        String username = JsonPath.read(mockMvc.perform(get("/api/me").header("Authorization", bearer(token)))
                .andReturn().getResponse().getContentAsString(), "$.username");

        // Uhr 8 Tage vorstellen und aufräumen (sonst geschieht das beim Start, täglich und vor jedem Demo-Login)
        Clock eightDaysLater = Clock.offset(Clock.system(ClockConfig.ZONE), Duration.ofDays(8));
        SandboxService laterService = new SandboxService(appUserRepository, demoPersona, eightDaysLater, 100, 7);
        new TransactionTemplate(transactionManager).executeWithoutResult(status -> laterService.deleteExpiredSandboxUsers());

        // Nutzer und sein eigenes Produkt sind weg, der gemeinsame Katalog bleibt
        assertThat(appUserRepository.findByUsername(username)).isEmpty();
        assertThat(productRepository.findById(ownProductId.longValue())).isEmpty();
        assertThat(productCatalog.get("Hafermilch")).isNotNull();

        // Der nächste Demo-Login funktioniert weiterhin
        demoToken();
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
