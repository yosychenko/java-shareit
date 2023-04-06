package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto createItem(
            @RequestHeader(value = "X-Sharer-User-Id") long userId,
            @Valid @RequestBody ItemDto newItemDto
    ) {
        Item createdItem = itemService.createItem(userId, ItemMapper.fromItemDto(newItemDto));
        return ItemMapper.toItemDto(createdItem);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(
            @PathVariable long itemId,
            @RequestHeader(value = "X-Sharer-User-Id") long userId,
            @Valid @RequestBody CommentDto commentDto
    ) {
        Comment comment = itemService.addComment(itemId, userId, CommentMapper.fromCommentDto(commentDto));
        return CommentMapper.toCommentDto(comment);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @PathVariable long itemId,
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody ItemDto newItemDto
    ) {
        Item updatedItem = itemService.updateItem(itemId, userId, ItemMapper.fromItemDto(newItemDto));
        return ItemMapper.toItemDto(updatedItem);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(
            @PathVariable long itemId,
            @RequestHeader("X-Sharer-User-Id") long userId
    ) {
        return itemService.getItemByIdWithBookingIntervals(itemId, userId);
    }

    @GetMapping
    Collection<ItemDto> getUserItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getUserItemsWithBookingIntervals(userId);
    }

    @GetMapping("/search")
    Collection<ItemDto> searchItems(@NotBlank @RequestParam String text) {
        return itemService.searchItems(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
