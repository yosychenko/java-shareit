package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    Item createItem(long userId, Item newItem);

    Comment addComment(long userId, long itemId, Comment newComment);

    Collection<Comment> getCommentsByItems(Collection<Item> items);

    Item updateItem(long userId, long itemId, Item newItem);

    Item getItemById(long itemId);

    ItemDto getItemByIdWithBookingIntervals(long userId, long itemId);

    Collection<Item> getUserItems(long userId);

    Collection<ItemDto> getUserItemsWithBookingIntervals(long userId);

    Collection<Item> searchItems(String text);
}
