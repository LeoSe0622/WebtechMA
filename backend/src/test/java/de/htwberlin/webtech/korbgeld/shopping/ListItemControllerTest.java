package de.htwberlin.webtech.korbgeld.shopping;

import de.htwberlin.webtech.korbgeld.common.JwtConfig;
import de.htwberlin.webtech.korbgeld.common.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ListItemController.class)
@Import({SecurityConfig.class, JwtConfig.class})
class ListItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ListItemService listItemService;

    @Test
    void getAllReturnsListItemsOfCurrentUserAsJson() throws Exception {
        when(listItemService.findAll(42L)).thenReturn(List.of(
                new ListItemResponse(1L, 10L, "Hafermilch", 2, false, Instant.parse("2026-10-07T10:00:00Z")),
                new ListItemResponse(2L, 11L, "Äpfel", 6, true, Instant.parse("2026-10-07T10:05:00Z"))
        ));

        // jwt(): simuliert ein gültiges Token mit Nutzer-ID 42 im Claim "sub"
        mockMvc.perform(get("/api/list-items").with(jwt().jwt(token -> token.subject("42"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].productName").value("Hafermilch"))
                .andExpect(jsonPath("$[0].productId").value(10))
                .andExpect(jsonPath("$[1].checked").value(true));
    }

    @Test
    void getAllWithoutTokenIsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/list-items"))
                .andExpect(status().isUnauthorized());
    }
}
