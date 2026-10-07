package de.htwberlin.webtech.korbgeld.shopping;

import de.htwberlin.webtech.korbgeld.auth.AppUser;
import de.htwberlin.webtech.korbgeld.auth.AppUserRepository;
import de.htwberlin.webtech.korbgeld.budget.BudgetService;
import de.htwberlin.webtech.korbgeld.common.ClockConfig;
import de.htwberlin.webtech.korbgeld.common.error.BadRequestException;
import de.htwberlin.webtech.korbgeld.pantry.PantryItem;
import de.htwberlin.webtech.korbgeld.pantry.PantryItemRepository;
import de.htwberlin.webtech.korbgeld.product.Product;
import de.htwberlin.webtech.korbgeld.product.ProductSource;
import de.htwberlin.webtech.korbgeld.shopping.PurchaseDtos.CompletePurchaseRequest;
import de.htwberlin.webtech.korbgeld.shopping.PurchaseDtos.CompletePurchaseResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PurchaseServiceTest {

    private static final Long USER_ID = 1L;

    private final ListItemRepository listItemRepository = mock(ListItemRepository.class);
    private final PurchaseRepository purchaseRepository = mock(PurchaseRepository.class);
    private final StoreRepository storeRepository = mock(StoreRepository.class);
    private final PantryItemRepository pantryItemRepository = mock(PantryItemRepository.class);
    private final AppUserRepository appUserRepository = mock(AppUserRepository.class);
    private final BudgetService budgetService = mock(BudgetService.class);
    private final Clock clock = Clock.fixed(LocalDate.of(2026, 10, 15).atStartOfDay(ClockConfig.ZONE).toInstant(),
            ClockConfig.ZONE);

    private final AppUser owner = new AppUser("mia", null, "Mia", 1, false, false, clock.instant());
    private final Product milk = product(10L, "Hafermilch");
    private final Product bread = product(11L, "Vollkornbrot");
    private PurchaseService purchaseService;

    @BeforeEach
    void setUp() {
        purchaseService = new PurchaseService(listItemRepository, purchaseRepository, storeRepository,
                pantryItemRepository, appUserRepository, budgetService, clock);
        when(appUserRepository.getReferenceById(USER_ID)).thenReturn(owner);
        when(storeRepository.findByOwnerIdAndNameIgnoreCase(USER_ID, "Lidl")).thenReturn(Optional.empty());
        when(storeRepository.save(any())).thenAnswer(call -> call.getArgument(0));
        when(purchaseRepository.save(any())).thenAnswer(call -> call.getArgument(0));
        when(budgetService.remaining(USER_ID, YearMonth.of(2026, 10))).thenReturn(new BigDecimal("187.66"));
    }

    @Test
    void completingWithoutCheckedItemsIsRejected() {
        when(listItemRepository.findAllByOwnerIdAndCheckedTrue(USER_ID)).thenReturn(List.of());

        assertThatThrownBy(() -> purchaseService.complete(USER_ID, request("12.34")))
                .isInstanceOf(BadRequestException.class);
        verify(purchaseRepository, never()).save(any());
    }

    @Test
    void completingCreatesPurchaseMergesPantryAndRemovesCheckedItems() {
        // Hafermilch zweimal abgehakt (1 + 2), Brot einmal
        List<ListItem> checked = List.of(item(milk, 1), item(milk, 2), item(bread, 1));
        when(listItemRepository.findAllByOwnerIdAndCheckedTrue(USER_ID)).thenReturn(checked);
        PantryItem milkInPantry = new PantryItem(owner, milk, 4, null, clock.instant());
        when(pantryItemRepository.findAllByOwnerIdAndProductId(USER_ID, 10L)).thenReturn(List.of(milkInPantry));
        when(pantryItemRepository.findAllByOwnerIdAndProductId(USER_ID, 11L)).thenReturn(List.of());

        CompletePurchaseResponse response = purchaseService.complete(USER_ID, request("12.34"));

        // Einkauf: zwei Positionen, Hafermilch zusammengefasst
        assertThat(response.purchase().totalAmount()).isEqualByComparingTo("12.34");
        assertThat(response.purchase().lines()).extracting("productId", "quantity")
                .containsExactly(org.assertj.core.groups.Tuple.tuple(10L, 3), org.assertj.core.groups.Tuple.tuple(11L, 1));
        assertThat(response.purchase().storeName()).isEqualTo("Lidl");
        assertThat(response.remainingBudget()).isEqualByComparingTo("187.66");

        // Vorrat: vorhandene Milch aufgestockt (4 + 3), Brot neu angelegt
        assertThat(milkInPantry.getQuantity()).isEqualTo(7);
        ArgumentCaptor<PantryItem> newPantry = ArgumentCaptor.forClass(PantryItem.class);
        verify(pantryItemRepository).save(newPantry.capture());
        assertThat(newPantry.getValue().getProduct()).isSameAs(bread);
        assertThat(newPantry.getValue().getQuantity()).isEqualTo(1);

        // Liste: abgehakte Einträge gelöscht
        verify(listItemRepository).deleteAll(checked);
    }

    private CompletePurchaseRequest request(String total) {
        return new CompletePurchaseRequest(null, "Lidl", new BigDecimal(total));
    }

    private ListItem item(Product product, int quantity) {
        ListItem item = new ListItem(owner, product, quantity, clock.instant());
        item.changeChecked(true);
        return item;
    }

    private static Product product(Long id, String name) {
        Product product = new Product(name, ProductSource.MANUAL);
        ReflectionTestUtils.setField(product, "id", id);   // IDs vergibt sonst die Datenbank
        return product;
    }
}
