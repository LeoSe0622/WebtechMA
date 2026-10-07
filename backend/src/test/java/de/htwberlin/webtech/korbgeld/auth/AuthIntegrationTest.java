package de.htwberlin.webtech.korbgeld.auth;

import com.jayway.jsonpath.JsonPath;
import de.htwberlin.webtech.korbgeld.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@Testcontainers(disabledWithoutDocker = true)
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void demoLoginCreatesSandboxUserWithPersonaData() throws Exception {
        String token = demoToken();

        mockMvc.perform(get("/api/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sandbox").value(true))
                .andExpect(jsonPath("$.displayName").value("Mia (Demo)"));

        // Persona „Mia“: 6 Einträge auf der Liste
        mockMvc.perform(get("/api/list-items").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6));
    }

    @Test
    void registerThenLoginWorksAndWrongPasswordIsRejected() throws Exception {
        String username = "nutzer-" + UUID.randomUUID().toString().substring(0, 8);
        String body = """
                {"username":"%s","password":"geheim-fuer-test","displayName":"Testperson","householdSize":2}
                """.formatted(username);

        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.householdSize").value(2))
                .andExpect(jsonPath("$.user.leaderboardOptIn").value(false));

        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"%s\",\"password\":\"geheim-fuer-test\"}".formatted(username)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());

        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"%s\",\"password\":\"falsch-falsch\"}".formatted(username)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));

        // Gleicher Name ein zweites Mal: 409
        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void registerWithInvalidHouseholdSizeReturnsFieldErrors() throws Exception {
        String body = """
                {"username":"x-%s","password":"geheim-fuer-test","displayName":"Testperson","householdSize":9}
                """.formatted(UUID.randomUUID().toString().substring(0, 6));

        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("householdSize"));
    }

    @Test
    void protectedEndpointWithoutTokenIsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/list-items")).andExpect(status().isUnauthorized());
    }

    @Test
    void workInProgressAndUnknownPathsReturnProblemDetail() throws Exception {
        String token = demoToken();

        mockMvc.perform(get("/api/recipes").header("Authorization", "Bearer " + token))
                .andExpect(status().isNotImplemented())
                .andExpect(jsonPath("$.status").value(501))
                .andExpect(jsonPath("$.feature").value("Rezepte"))
                .andExpect(jsonPath("$.milestone").value("nach M4"));

        mockMvc.perform(get("/api/gibt-es-nicht").header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.path").value(startsWith("/api/gibt-es-nicht")));
    }

    private String demoToken() throws Exception {
        String response = mockMvc.perform(post("/api/auth/demo"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(response, "$.token");
    }
}
