package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequest createItemRequest(long userId, ItemRequest newItemRequest);

    Collection<ItemRequest> getAllItemRequests(long userId);

    Collection<ItemRequest> getItemRequestsPaginated(long userId, int from, int size);

    ItemRequest getItemRequestById(long userId, long itemRequestId);
}
