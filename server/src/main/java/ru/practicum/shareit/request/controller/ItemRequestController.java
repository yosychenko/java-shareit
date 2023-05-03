package ru.practicum.shareit.request.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto createItemRequest(
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
    public Collection<ItemRequestResponseDto> getAllItemRequestsFromUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.getAllItemRequestsFromUser(userId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestResponseDto> getAllOtherUsersRequests(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "2000") int size
    ) {
        return itemRequestService.getAllOtherUsersRequests(userId, PageRequest.of(from, size));
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto getItemRequestById(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long requestId
    ) {
        return itemRequestService.getItemRequestByIdFullInfo(userId, requestId);
    }
}
