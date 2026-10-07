package de.htwberlin.webtech.korbgeld.shopping;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListItemService {

    private final ListItemRepository listItemRepository;

    public ListItemService(ListItemRepository listItemRepository) {
        this.listItemRepository = listItemRepository;
    }

    @Transactional(readOnly = true)
    public List<ListItemResponse> findAll() {
        return listItemRepository.findAllByOrderByCreatedAtAsc().stream()
                .map(ListItemResponse::from)
                .toList();
    }
}
