package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> storage;

    private long idCounter;

    public InMemoryItemStorage() {
        this.storage = new HashMap<>();
    }

    @Override
    public Item createItem(Item newItem) {
        newItem.setId(++idCounter);
        storage.put(newItem.getId(), newItem);

        return getItemById(newItem.getId());
    }

    @Override
    public Item updateItem(Item newItem) {
        storage.put(newItem.getId(), newItem);

        return getItemById(newItem.getId());
    }

    @Override
    public Item getItemById(long itemId) {
        Item item = storage.get(itemId);

        if (item == null) {
            throw new ItemNotFoundException(itemId);
        }

        return item;
    }

    @Override
    public Collection<Item> getUserItems(long userId) {
        return storage.values().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Item> searchItems(String query) {
        if (query.isBlank()) {
            return List.of();
        }

        String clearedQuery = query.toLowerCase();

        return storage.values().stream()
                .filter(Item::getAvailable)
                .filter(item ->
                        item.getName().toLowerCase().contains(clearedQuery) ||
                                item.getDescription().toLowerCase().contains(clearedQuery)
                )
                .collect(Collectors.toList());
    }
}
