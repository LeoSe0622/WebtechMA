package de.htwberlin.webtech.korbgeld.shopping;

import de.htwberlin.webtech.korbgeld.TestcontainersConfiguration;
import de.htwberlin.webtech.korbgeld.product.Product;
import de.htwberlin.webtech.korbgeld.product.ProductRepository;
import de.htwberlin.webtech.korbgeld.product.ProductSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Ganze Kette mit echter Postgres im Testcontainer: Controller → Service → Repository → SQL
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@Testcontainers(disabledWithoutDocker = true)
class ListItemIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ListItemRepository listItemRepository;

    @BeforeEach
    void cleanDatabase() {
        listItemRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    void returnsStoredItemsOldestFirstWithProductName() throws Exception {
        Product milk = productRepository.save(new Product("Hafermilch", ProductSource.MANUAL));
        Product bread = productRepository.save(new Product("Vollkornbrot", ProductSource.MANUAL));
        listItemRepository.save(new ListItem(bread, 1, Instant.parse("2026-10-07T10:05:00Z")));
        listItemRepository.save(new ListItem(milk, 2, Instant.parse("2026-10-07T10:00:00Z")));

        mockMvc.perform(get("/api/list-items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].productName").value("Hafermilch"))
                .andExpect(jsonPath("$[0].productId").value(milk.getId()))
                .andExpect(jsonPath("$[0].quantity").value(2))
                .andExpect(jsonPath("$[0].checked").value(false))
                .andExpect(jsonPath("$[0].createdAt").value("2026-10-07T10:00:00Z"))
                .andExpect(jsonPath("$[1].productName").value("Vollkornbrot"));
    }
}
