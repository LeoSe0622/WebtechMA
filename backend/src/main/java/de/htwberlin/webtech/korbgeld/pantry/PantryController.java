package de.htwberlin.webtech.korbgeld.pantry;

import de.htwberlin.webtech.korbgeld.common.CurrentUser;
import de.htwberlin.webtech.korbgeld.pantry.PantryDtos.ConsumeRequest;
import de.htwberlin.webtech.korbgeld.pantry.PantryDtos.PantryItemResponse;
import de.htwberlin.webtech.korbgeld.pantry.PantryDtos.UpdatePantryItemRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pantry-items")
public class PantryController {

    private final PantryService pantryService;

    public PantryController(PantryService pantryService) {
        this.pantryService = pantryService;
    }

    @GetMapping
    public List<PantryItemResponse> getAll(@AuthenticationPrincipal Jwt jwt) {
        return pantryService.findAll(CurrentUser.id(jwt));
    }

    @PatchMapping("/{id}")
    public PantryItemResponse update(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id,
                                     @Valid @RequestBody UpdatePantryItemRequest request) {
        return pantryService.update(CurrentUser.id(jwt), id, request);
    }

    // 200 mit dem geänderten Eintrag, oder 204, wenn er aufgebraucht und gelöscht ist
    @PostMapping("/{id}/consume")
    public ResponseEntity<PantryItemResponse> consume(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id,
                                                      @Valid @RequestBody(required = false) ConsumeRequest request) {
        int amount = request == null || request.amount() == null ? 1 : request.amount();
        return pantryService.consume(CurrentUser.id(jwt), id, amount)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}
