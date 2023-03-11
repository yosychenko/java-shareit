package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Map;

public interface ItemService {
    Item createItem(long userId, Item newItem);

    Item updateItem(long itemId, long userId, Map<String, Object> patch);

    Item getItemById(long itemId);

    Collection<Item> getUserItems(long userId);

    Collection<Item> searchItems(String text);
}
