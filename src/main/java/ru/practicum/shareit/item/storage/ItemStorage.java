package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {

    Item createItem(Item newItem);

    Item updateItem(Item newItem);

    Item getItemById(long itemId);

    Collection<Item> getUserItems(long userId);

    Collection<Item> searchItems(String query);
}
