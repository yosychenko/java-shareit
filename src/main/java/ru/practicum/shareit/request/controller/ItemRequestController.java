package ru.practicum.shareit.request.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    ItemRequestDto createItemRequest(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody ItemRequestDto itemRequestDto
    ) {
        ItemRequest itemRequest = itemRequestService.createItemRequest(
                userId,
                ItemRequestMapper.fromItemRequestDto(itemRequestDto)
        );

        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @GetMapping
    Collection<ItemRequestDto> getAllItemRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        Collection<ItemRequest> itemRequests = itemRequestService.getAllItemRequests(userId);

        return itemRequests.stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/all")
    Collection<ItemRequestDto> getItemRequestsPaginated(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam int from,
            @RequestParam int size
    ) {
        return null;
    }

    @GetMapping("/{requestId}")
    ItemRequestDto getItemRequestById(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long requestId
    ) {
        return null;
    }
}
