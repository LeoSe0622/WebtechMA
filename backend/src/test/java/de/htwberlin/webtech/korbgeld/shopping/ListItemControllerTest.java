package de.htwberlin.webtech.korbgeld.shopping;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ListItemController.class)
class ListItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ListItemService listItemService;

    @Test
    void getAllReturnsListItemsAsJson() throws Exception {
        when(listItemService.findAll()).thenReturn(List.of(
                new ListItemResponse(1L, 10L, "Hafermilch", 2, false, Instant.parse("2026-10-07T10:00:00Z")),
                new ListItemResponse(2L, 11L, "Äpfel", 6, true, Instant.parse("2026-10-07T10:05:00Z"))
        ));

        mockMvc.perform(get("/api/list-items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].productName").value("Hafermilch"))
                .andExpect(jsonPath("$[0].productId").value(10))
                .andExpect(jsonPath("$[1].checked").value(true));
    }
}
