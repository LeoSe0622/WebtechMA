package de.htwberlin.webtech.korbgeld.shopping;

import de.htwberlin.webtech.korbgeld.TestcontainersConfiguration;
import de.htwberlin.webtech.korbgeld.auth.AppUser;
import de.htwberlin.webtech.korbgeld.auth.AppUserRepository;
import de.htwberlin.webtech.korbgeld.auth.TokenService;
import de.htwberlin.webtech.korbgeld.product.Product;
import de.htwberlin.webtech.korbgeld.product.ProductRepository;
import de.htwberlin.webtech.korbgeld.product.ProductSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Ganze Kette mit echter Postgres im Testcontainer: Token → Controller → Service → Repository → SQL
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@Testcontainers(disabledWithoutDocker = true)
class ListItemIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ListItemRepository listItemRepository;

    @Autowired
    private TokenService tokenService;

    @Test
    void returnsOnlyOwnItemsOldestFirstWithProductName() throws Exception {
        AppUser alice = newUser("alice");
        AppUser bob = newUser("bob");
        Product milk = productRepository.save(new Product("Testmilch " + UUID.randomUUID(), ProductSource.MANUAL));
        Product bread = productRepository.save(new Product("Testbrot " + UUID.randomUUID(), ProductSource.MANUAL));
        listItemRepository.save(new ListItem(alice, bread, 1, Instant.parse("2026-10-07T10:05:00Z")));
        listItemRepository.save(new ListItem(alice, milk, 2, Instant.parse("2026-10-07T10:00:00Z")));
        listItemRepository.save(new ListItem(bob, milk, 9, Instant.parse("2026-10-07T09:00:00Z")));

        mockMvc.perform(get("/api/list-items").header("Authorization", "Bearer " + tokenService.createToken(alice)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].productName").value(milk.getName()))
                .andExpect(jsonPath("$[0].productId").value(milk.getId()))
                .andExpect(jsonPath("$[0].quantity").value(2))
                .andExpect(jsonPath("$[0].createdAt").value("2026-10-07T10:00:00Z"))
                .andExpect(jsonPath("$[1].productName").value(bread.getName()));
    }

    private AppUser newUser(String name) {
        String username = name + "-" + UUID.randomUUID().toString().substring(0, 8);
        return appUserRepository.save(new AppUser(username, null, name, 1, false, false, Instant.now()));
    }
}
