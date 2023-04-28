package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

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
            @RequestBody ItemDto newItemDto
    ) {
        Item createdItem = itemService.createItem(userId, newItemDto);
        return ItemMapper.toItemDto(createdItem);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(
            @RequestHeader(value = "X-Sharer-User-Id") long userId,
            @PathVariable long itemId,
            @RequestBody CommentDto commentDto
    ) {
        Comment comment = itemService.addComment(userId, itemId, CommentMapper.fromCommentDto(commentDto));
        return CommentMapper.toCommentDto(comment);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long itemId,
            @RequestBody ItemDto newItemDto
    ) {
        Item updatedItem = itemService.updateItem(userId, itemId, ItemMapper.fromItemDto(newItemDto));
        return ItemMapper.toItemDto(updatedItem);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long itemId
    ) {
        return itemService.getItemByIdWithBookingIntervals(userId, itemId);
    }

    @GetMapping
    Collection<ItemDto> getUserItems(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "2000") int size
    ) {
        return itemService.getUserItemsWithBookingIntervals(userId, PageRequest.of(from, size));
    }

    @GetMapping("/search")
    Collection<ItemDto> searchItems(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @NotBlank @RequestParam String text,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "2000") int size
    ) {
        return itemService.searchItems(text, PageRequest.of(from, size)).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
