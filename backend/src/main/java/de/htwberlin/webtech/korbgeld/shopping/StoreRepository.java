package de.htwberlin.webtech.korbgeld.shopping;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {

    List<Store> findAllByOwnerIdOrderByNameAsc(Long ownerId);

    Optional<Store> findByIdAndOwnerId(Long id, Long ownerId);

    Optional<Store> findByOwnerIdAndNameIgnoreCase(Long ownerId, String name);
}
