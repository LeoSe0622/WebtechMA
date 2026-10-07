package de.htwberlin.webtech.korbgeld.shopping;

import de.htwberlin.webtech.korbgeld.auth.AppUserRepository;
import de.htwberlin.webtech.korbgeld.common.error.ConflictException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StoreService {

    public record StoreResponse(Long id, String name) {

        static StoreResponse from(Store store) {
            return new StoreResponse(store.getId(), store.getName());
        }
    }

    private final StoreRepository storeRepository;
    private final AppUserRepository appUserRepository;

    public StoreService(StoreRepository storeRepository, AppUserRepository appUserRepository) {
        this.storeRepository = storeRepository;
        this.appUserRepository = appUserRepository;
    }

    @Transactional(readOnly = true)
    public List<StoreResponse> findAll(Long userId) {
        return storeRepository.findAllByOwnerIdOrderByNameAsc(userId).stream()
                .map(StoreResponse::from)
                .toList();
    }

    @Transactional
    public StoreResponse create(Long userId, String rawName) {
        String name = rawName.trim();
        if (storeRepository.findByOwnerIdAndNameIgnoreCase(userId, name).isPresent()) {
            throw new ConflictException("Diesen Laden gibt es schon.");
        }
        return StoreResponse.from(storeRepository.save(new Store(appUserRepository.getReferenceById(userId), name)));
    }
}
