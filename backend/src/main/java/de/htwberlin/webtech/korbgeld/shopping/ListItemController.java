package de.htwberlin.webtech.korbgeld.shopping;

import de.htwberlin.webtech.korbgeld.common.CurrentUser;
import de.htwberlin.webtech.korbgeld.shopping.ListItemDtos.CreateListItemRequest;
import de.htwberlin.webtech.korbgeld.shopping.ListItemDtos.CreateListItemResponse;
import de.htwberlin.webtech.korbgeld.shopping.ListItemDtos.UpdateListItemRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/list-items")
public class ListItemController {

    private final ListItemService listItemService;

    public ListItemController(ListItemService listItemService) {
        this.listItemService = listItemService;
    }

    @GetMapping
    public List<ListItemResponse> getAll(@AuthenticationPrincipal Jwt jwt) {
        return listItemService.findAll(CurrentUser.id(jwt));
    }

    // 201 mit dem neuen Eintrag, oder 200 mit alreadyInPantry, wenn das Produkt schon im Vorrat steht
    @PostMapping
    public ResponseEntity<CreateListItemResponse> create(@AuthenticationPrincipal Jwt jwt,
                                                         @Valid @RequestBody CreateListItemRequest request) {
        CreateListItemResponse response = listItemService.create(CurrentUser.id(jwt), request);
        HttpStatus status = response.item() != null ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @PatchMapping("/{id}")
    public ListItemResponse update(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id,
                                   @Valid @RequestBody UpdateListItemRequest request) {
        return listItemService.update(CurrentUser.id(jwt), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        listItemService.delete(CurrentUser.id(jwt), id);
    }
}
