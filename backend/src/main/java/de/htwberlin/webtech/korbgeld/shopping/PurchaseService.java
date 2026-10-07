package de.htwberlin.webtech.korbgeld.shopping;

import de.htwberlin.webtech.korbgeld.auth.AppUser;
import de.htwberlin.webtech.korbgeld.auth.AppUserRepository;
import de.htwberlin.webtech.korbgeld.budget.BudgetService;
import de.htwberlin.webtech.korbgeld.common.error.BadRequestException;
import de.htwberlin.webtech.korbgeld.common.error.NotFoundException;
import de.htwberlin.webtech.korbgeld.pantry.PantryItem;
import de.htwberlin.webtech.korbgeld.pantry.PantryItemRepository;
import de.htwberlin.webtech.korbgeld.product.Product;
import de.htwberlin.webtech.korbgeld.shopping.PurchaseDtos.CompletePurchaseRequest;
import de.htwberlin.webtech.korbgeld.shopping.PurchaseDtos.CompletePurchaseResponse;
import de.htwberlin.webtech.korbgeld.shopping.PurchaseDtos.PurchaseResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class PurchaseService {

    private final ListItemRepository listItemRepository;
    private final PurchaseRepository purchaseRepository;
    private final StoreRepository storeRepository;
    private final PantryItemRepository pantryItemRepository;
    private final AppUserRepository appUserRepository;
    private final BudgetService budgetService;
    private final Clock clock;

    public PurchaseService(ListItemRepository listItemRepository, PurchaseRepository purchaseRepository,
                           StoreRepository storeRepository, PantryItemRepository pantryItemRepository,
                           AppUserRepository appUserRepository, BudgetService budgetService, Clock clock) {
        this.listItemRepository = listItemRepository;
        this.purchaseRepository = purchaseRepository;
        this.storeRepository = storeRepository;
        this.pantryItemRepository = pantryItemRepository;
        this.appUserRepository = appUserRepository;
        this.budgetService = budgetService;
        this.clock = clock;
    }

    /**
     * Einkauf abschließen (UC4). Alles in einer Transaktion: Scheitert ein Schritt, wird nichts gespeichert.
     */
    @Transactional
    public CompletePurchaseResponse complete(Long userId, CompletePurchaseRequest request) {
        List<ListItem> checked = listItemRepository.findAllByOwnerIdAndCheckedTrue(userId);
        if (checked.isEmpty()) {
            throw new BadRequestException("Hake mindestens einen Artikel ab, bevor du den Einkauf abschließt.");
        }
        AppUser owner = appUserRepository.getReferenceById(userId);
        Store store = resolveStore(userId, owner, request);

        // Gleiches Produkt mehrfach abgehakt: Mengen zusammenzählen
        Map<Product, Integer> quantities = new LinkedHashMap<>();
        for (ListItem item : checked) {
            quantities.merge(item.getProduct(), item.getQuantity(), Integer::sum);
        }

        // 1. Einkauf mit seinen Positionen anlegen
        LocalDate today = LocalDate.now(clock);
        List<PurchaseLine> lines = quantities.entrySet().stream()
                .map(entry -> new PurchaseLine(entry.getKey().getId(), entry.getValue()))
                .toList();
        Purchase purchase = purchaseRepository.save(new Purchase(owner, store, today, request.totalAmount(), lines));

        // 2. Vorrat erhöhen: vorhandenen Eintrag des Produkts aufstocken, sonst neu anlegen
        quantities.forEach((product, quantity) -> addToPantry(userId, owner, product, quantity));

        // 3. Abgehakte Einträge von der Liste löschen
        listItemRepository.deleteAll(checked);

        return new CompletePurchaseResponse(PurchaseResponse.from(purchase),
                budgetService.remaining(userId, YearMonth.from(today)));
    }

    @Transactional(readOnly = true)
    public List<PurchaseResponse> findByMonth(Long userId, YearMonth month) {
        return purchaseRepository.findAllByOwnerIdAndDateBetweenOrderByDateDesc(userId, month.atDay(1),
                        month.atEndOfMonth()).stream()
                .map(PurchaseResponse::from)
                .toList();
    }

    private Store resolveStore(Long userId, AppUser owner, CompletePurchaseRequest request) {
        if (request.storeId() != null) {
            return storeRepository.findByIdAndOwnerId(request.storeId(), userId)
                    .orElseThrow(() -> new NotFoundException("Diesen Laden gibt es nicht."));
        }
        if (request.storeName() == null || request.storeName().isBlank()) {
            throw new BadRequestException("Bitte wähle einen Laden aus oder gib einen neuen ein.");
        }
        String name = request.storeName().trim();
        return storeRepository.findByOwnerIdAndNameIgnoreCase(userId, name)
                .orElseGet(() -> storeRepository.save(new Store(owner, name)));
    }

    // Zusammenfassen nur mit einem Eintrag ohne Haltbarkeitsdatum: Frisch Gekauftes soll nicht in einem
    // (womöglich abgelaufenen) Eintrag mit altem Datum landen (Review 02, m5; E18)
    private void addToPantry(Long userId, AppUser owner, Product product, int quantity) {
        pantryItemRepository.findAllByOwnerIdAndProductId(userId, product.getId()).stream()
                .filter(item -> item.getBestBefore() == null)
                .findFirst()
                .ifPresentOrElse(
                        item -> item.increaseQuantity(quantity),
                        () -> pantryItemRepository.save(new PantryItem(owner, product, quantity, null, clock.instant())));
    }
}
