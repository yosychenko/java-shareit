package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.PageableIsNotValidException;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.pagination.PageableAdjuster;
import ru.practicum.shareit.pagination.PageableValidator;

import javax.validation.Valid;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(
            @RequestHeader(value = "X-Sharer-User-Id") long userId,
            @Valid @RequestBody ItemDto newItemDto
    ) {
        return itemClient.createItem(userId, newItemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @RequestHeader(value = "X-Sharer-User-Id") long userId,
            @PathVariable long itemId,
            @Valid @RequestBody CommentDto commentDto
    ) {
        return itemClient.addComment(userId, itemId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long itemId,
            @RequestBody ItemDto newItemDto
    ) {
        return itemClient.updateItem(userId, itemId, newItemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long itemId
    ) {
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "2000") int size
    ) {
        if (!PageableValidator.isValid(from, size)) {
            throw new PageableIsNotValidException();
        }

        int newFrom = PageableAdjuster.adjustFrom(from, size);

        return itemClient.getUserItems(userId, newFrom, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam String text,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "2000") int size
    ) {
        if (!PageableValidator.isValid(from, size)) {
            throw new PageableIsNotValidException();
        }

        int newFrom = PageableAdjuster.adjustFrom(from, size);

        return itemClient.searchItems(userId, text, newFrom, size);
    }
}
