package de.htwberlin.webtech.korbgeld;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Die lesenden Demo-Bereiche aus Phase 4 mit echten Seed-Daten. */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@Testcontainers(disabledWithoutDocker = true)
class DemoAreasIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void habitsOfThePersonaAreListedByDueDate() throws Exception {
        mockMvc.perform(get("/api/habits").header("Authorization", bearer(demoToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].productName").value("Bananen"));   // fällig in 1 Tag
    }

    @Test
    void leaderboardShowsSeedUsersSortedAndMyPossiblePlace() throws Exception {
        String json = mockMvc.perform(get("/api/leaderboard").header("Authorization", bearer(demoToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(greaterThanOrEqualTo(15)))
                .andExpect(jsonPath("$.entries[0].rank").value(1))
                .andExpect(jsonPath("$.yourPosition").isNumber())
                .andReturn().getResponse().getContentAsString();

        // Sparquoten absteigend sortiert
        List<Object> rates = JsonPath.read(json, "$.entries[*].savingsRate");
        for (int i = 1; i < rates.size(); i++) {
            assertThat(new BigDecimal(rates.get(i - 1).toString()))
                    .isGreaterThanOrEqualTo(new BigDecimal(rates.get(i).toString()));
        }
        // Demo-Nutzer stehen nie in der Liste
        List<String> names = JsonPath.read(json, "$.entries[*].displayName");
        assertThat(names).doesNotContain("Mia (Demo)");
    }

    @Test
    void portfoliosHaveWeightsAddingUpToOne() throws Exception {
        String json = mockMvc.perform(get("/api/invest/portfolios").header("Authorization", bearer(demoToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[1].id").value("AUSGEWOGEN"))
                .andReturn().getResponse().getContentAsString();

        for (int p = 0; p < 3; p++) {
            List<Object> shares = JsonPath.read(json, "$[" + p + "].weights[*].share");
            BigDecimal sum = shares.stream().map(s -> new BigDecimal(s.toString())).reduce(BigDecimal.ZERO, BigDecimal::add);
            assertThat(sum).isEqualByComparingTo("1");
        }
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
