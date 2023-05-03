package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.PageableIsNotValidException;
import ru.practicum.shareit.pagination.PageableAdjuster;
import ru.practicum.shareit.pagination.PageableValidator;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Valid @RequestBody ItemRequestDto itemRequestDto
    ) {
        return itemRequestClient.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemRequestsFromUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestClient.getAllItemRequestsFromUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllOtherUsersRequests(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "2000") int size
    ) {
        if (!PageableValidator.isValid(from, size)) {
            throw new PageableIsNotValidException();
        }

        int newFrom = PageableAdjuster.adjustFrom(from, size);

        return itemRequestClient.getAllOtherUsersRequests(userId, newFrom, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long requestId
    ) {
        return itemRequestClient.getItemRequestById(userId, requestId);
    }
}
