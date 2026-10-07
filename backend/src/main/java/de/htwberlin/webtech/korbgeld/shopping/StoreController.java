package de.htwberlin.webtech.korbgeld.shopping;

import de.htwberlin.webtech.korbgeld.common.CurrentUser;
import de.htwberlin.webtech.korbgeld.shopping.StoreService.StoreResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
public class StoreController {

    public record StoreRequest(@NotBlank @Size(max = 60) String name) {
    }

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping
    public List<StoreResponse> getAll(@AuthenticationPrincipal Jwt jwt) {
        return storeService.findAll(CurrentUser.id(jwt));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StoreResponse create(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody StoreRequest request) {
        return storeService.create(CurrentUser.id(jwt), request.name());
    }
}
