package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    Item createItem(long userId, Item newItem);

    Comment addComment(long itemId, long userId, Comment newComment);

    Item updateItem(long itemId, long userId, Item newItem);

    Item getItemById(long itemId);

    Collection<Item> getUserItems(long userId);

    Collection<Item> searchItems(String text);
}
