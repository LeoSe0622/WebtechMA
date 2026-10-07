package de.htwberlin.webtech.korbgeld.shopping;

import de.htwberlin.webtech.korbgeld.auth.AppUserRepository;
import de.htwberlin.webtech.korbgeld.common.CurrentUser;
import de.htwberlin.webtech.korbgeld.common.error.ConflictException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Läden des Nutzers. So wenig Logik, dass ein eigener Service unnötig wäre. */
@RestController
@RequestMapping("/api/stores")
public class StoreController {

    public record StoreRequest(@NotBlank @Size(max = 60) String name) {
    }

    public record StoreResponse(Long id, String name) {

        static StoreResponse from(Store store) {
            return new StoreResponse(store.getId(), store.getName());
        }
    }

    private final StoreRepository storeRepository;
    private final AppUserRepository appUserRepository;

    public StoreController(StoreRepository storeRepository, AppUserRepository appUserRepository) {
        this.storeRepository = storeRepository;
        this.appUserRepository = appUserRepository;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<StoreResponse> getAll(@AuthenticationPrincipal Jwt jwt) {
        return storeRepository.findAllByOwnerIdOrderByNameAsc(CurrentUser.id(jwt)).stream()
                .map(StoreResponse::from)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public StoreResponse create(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody StoreRequest request) {
        Long userId = CurrentUser.id(jwt);
        String name = request.name().trim();
        if (storeRepository.findByOwnerIdAndNameIgnoreCase(userId, name).isPresent()) {
            throw new ConflictException("Diesen Laden gibt es schon.");
        }
        return StoreResponse.from(storeRepository.save(new Store(appUserRepository.getReferenceById(userId), name)));
    }
}
