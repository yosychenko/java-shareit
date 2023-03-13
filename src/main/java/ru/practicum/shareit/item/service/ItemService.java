package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    Item createItem(long userId, Item newItem);

    Item updateItem(long itemId, long userId, ItemDto newItem);

    Item getItemById(long itemId);

    Collection<Item> getUserItems(long userId);

    Collection<Item> searchItems(String text);
}
