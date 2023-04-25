package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequest createItemRequest(long userId, ItemRequest newItemRequest);

    Collection<ItemRequestResponseDto> getAllItemRequestsFromUser(long userId);

    Collection<ItemRequestResponseDto> getAllOtherUsersRequests(long userId, Pageable pageable);

    ItemRequestResponseDto getItemRequestByIdFullInfo(long userId, long itemRequestId);

    ItemRequest getItemRequestById(long itemRequestId);
}
