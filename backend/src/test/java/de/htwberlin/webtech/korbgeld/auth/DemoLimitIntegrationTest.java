package de.htwberlin.webtech.korbgeld.auth;

import de.htwberlin.webtech.korbgeld.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Eigener Kontext mit Limit 2 pro Tag, damit der Test schnell an die Grenze kommt
@SpringBootTest(properties = "app.demo.max-per-day=2")
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@Testcontainers(disabledWithoutDocker = true)
class DemoLimitIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void thirdDemoLoginOnSameDayIsRejectedWith429() throws Exception {
        mockMvc.perform(post("/api/auth/demo")).andExpect(status().isCreated());
        mockMvc.perform(post("/api/auth/demo")).andExpect(status().isCreated());
        mockMvc.perform(post("/api/auth/demo")).andExpect(status().isTooManyRequests());
    }
}
