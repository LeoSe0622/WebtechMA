package de.htwberlin.webtech.korbgeld.shopping;

import de.htwberlin.webtech.korbgeld.auth.AppUserRepository;
import de.htwberlin.webtech.korbgeld.common.error.BadRequestException;
import de.htwberlin.webtech.korbgeld.common.error.NotFoundException;
import de.htwberlin.webtech.korbgeld.pantry.PantryItem;
import de.htwberlin.webtech.korbgeld.pantry.PantryItemRepository;
import de.htwberlin.webtech.korbgeld.product.Product;
import de.htwberlin.webtech.korbgeld.product.ProductRepository;
import de.htwberlin.webtech.korbgeld.shopping.ListItemDtos.CreateListItemRequest;
import de.htwberlin.webtech.korbgeld.shopping.ListItemDtos.CreateListItemResponse;
import de.htwberlin.webtech.korbgeld.shopping.ListItemDtos.PantryHint;
import de.htwberlin.webtech.korbgeld.shopping.ListItemDtos.UpdateListItemRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class ListItemService {

    private final ListItemRepository listItemRepository;
    private final ProductRepository productRepository;
    private final PantryItemRepository pantryItemRepository;
    private final AppUserRepository appUserRepository;
    private final Clock clock;

    public ListItemService(ListItemRepository listItemRepository, ProductRepository productRepository,
                           PantryItemRepository pantryItemRepository, AppUserRepository appUserRepository,
                           Clock clock) {
        this.listItemRepository = listItemRepository;
        this.productRepository = productRepository;
        this.pantryItemRepository = pantryItemRepository;
        this.appUserRepository = appUserRepository;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public List<ListItemResponse> findAll(Long userId) {
        return listItemRepository.findAllByOwnerIdOrderByCreatedAtAsc(userId).stream()
                .map(ListItemResponse::from)
                .toList();
    }

    @Transactional
    public CreateListItemResponse create(Long userId, CreateListItemRequest request) {
        Product product = resolveProduct(userId, request);

        // Vorrats-Warnung: Steht das Produkt schon im Vorrat, erst nachfragen (außer force)
        Optional<PantryHint> inPantry = pantryHint(userId, product.getId());
        if (inPantry.isPresent() && !request.forced()) {
            return new CreateListItemResponse(null, inPantry.get());
        }

        ListItem item = listItemRepository.save(new ListItem(appUserRepository.getReferenceById(userId), product,
                request.quantity(), clock.instant()));
        return new CreateListItemResponse(ListItemResponse.from(item), null);
    }

    @Transactional
    public ListItemResponse update(Long userId, Long id, UpdateListItemRequest request) {
        ListItem item = findOwn(userId, id);
        if (request.checked() != null) {
            item.changeChecked(request.checked());
        }
        if (request.quantity() != null) {
            item.changeQuantity(request.quantity());
        }
        return ListItemResponse.from(item);   // Änderungen speichert JPA am Ende der Transaktion selbst
    }

    @Transactional
    public void delete(Long userId, Long id) {
        listItemRepository.delete(findOwn(userId, id));
    }

    private ListItem findOwn(Long userId, Long id) {
        // Fremde Einträge gelten als „nicht gefunden“, damit niemand fremde IDs ausprobieren kann
        return listItemRepository.findByIdAndOwnerId(id, userId)
                .orElseThrow(() -> new NotFoundException("Diesen Listeneintrag gibt es nicht."));
    }

    // Abgleich über die productId; ohne ID: eigenes oder gemeinsames gleichnamiges Produkt, sonst neu (E14, E17)
    private Product resolveProduct(Long userId, CreateListItemRequest request) {
        if (request.productId() != null) {
            return productRepository.findVisible(request.productId(), userId)
                    .orElseThrow(() -> new NotFoundException("Dieses Produkt gibt es nicht."));
        }
        if (request.productName() == null || request.productName().isBlank()) {
            throw new BadRequestException("Bitte ein Produkt auswählen oder einen Namen eingeben.");
        }
        String name = request.productName().trim();
        return productRepository.findFirstByCreatedByAndNameIgnoreCaseOrderByIdAsc(userId, name)
                .or(() -> productRepository.findFirstByCreatedByIsNullAndNameIgnoreCaseOrderByIdAsc(name))
                .orElseGet(() -> productRepository.save(Product.ownedBy(userId, name)));
    }

    private Optional<PantryHint> pantryHint(Long userId, Long productId) {
        List<PantryItem> entries = pantryItemRepository.findAllByOwnerIdAndProductId(userId, productId);
        int quantity = entries.stream().mapToInt(PantryItem::getQuantity).sum();
        if (quantity <= 0) {
            return Optional.empty();
        }
        // Frühestes Mindesthaltbarkeitsdatum zeigen, damit man sieht, ob es bald weg muss
        var bestBefore = entries.stream()
                .map(PantryItem::getBestBefore)
                .filter(date -> date != null)
                .min(Comparator.naturalOrder())
                .orElse(null);
        return Optional.of(new PantryHint(quantity, bestBefore));
    }
}
