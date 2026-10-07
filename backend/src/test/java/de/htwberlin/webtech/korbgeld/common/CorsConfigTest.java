package de.htwberlin.webtech.korbgeld.common;

import de.htwberlin.webtech.korbgeld.shopping.ListItemController;
import de.htwberlin.webtech.korbgeld.shopping.ListItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Erlaubter Origin kommt aus application-test.yml (http://localhost:5173)
@WebMvcTest(ListItemController.class)
@Import({SecurityConfig.class, JwtConfig.class})
class CorsConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ListItemService listItemService;

    @Test
    void allowsConfiguredFrontendOrigin() throws Exception {
        mockMvc.perform(get("/api/list-items").with(jwt().jwt(token -> token.subject("1"))).header("Origin", "http://localhost:5173"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }

    @Test
    void answersPreflightForPutWithAuthorizationHeaderWithoutToken() throws Exception {
        // Vor PUT, PATCH und DELETE fragt der Browser per OPTIONS, ob er darf. Der Preflight hat nie ein Token.
        mockMvc.perform(options("/api/budgets/2026-10")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "PUT")
                        .header("Access-Control-Request-Headers", "Authorization, Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"))
                .andExpect(header().string("Access-Control-Allow-Methods", containsString("PUT")))
                .andExpect(header().string("Access-Control-Allow-Headers", containsString("Authorization")));
    }

    @Test
    void rejectsUnknownOrigin() throws Exception {
        mockMvc.perform(get("/api/list-items").with(jwt().jwt(token -> token.subject("1"))).header("Origin", "https://evil.example"))
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }
}
