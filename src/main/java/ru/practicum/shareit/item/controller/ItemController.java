package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    private final BookingService bookingService;


    @Autowired
    public ItemController(ItemService itemService, BookingService bookingService) {
        this.itemService = itemService;
        this.bookingService = bookingService;
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
        Item item = itemService.getItemById(itemId);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        Collection<Comment> comments = itemService.getCommentsByItem(item);
        itemDto.setComments(comments.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList()));

        if (item.getOwner().getId() == userId) {
            Booking lastBooking = bookingService.getLastItemBooking(item);
            Booking nextBooking = bookingService.getNextItemBooking(item);

            if (lastBooking != null) {
                itemDto.setLastBooking(BookingMapper.toBookingTimeIntervalDto(lastBooking));
            }
            if (nextBooking != null) {
                itemDto.setNextBooking(BookingMapper.toBookingTimeIntervalDto(nextBooking));
            }
        }

        return itemDto;
    }

    @GetMapping
    Collection<ItemDto> getUserItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        Collection<Item> items = itemService.getUserItems(userId);
        List<ItemDto> itemDtos = new ArrayList<>();
        List<ItemDto> itemDtosNullIntervals = new ArrayList<>();

        for (Item item : items) {
            ItemDto itemDto = ItemMapper.toItemDto(item);
            Booking lastBooking = bookingService.getLastItemBooking(item);
            Booking nextBooking = bookingService.getNextItemBooking(item);

            if (lastBooking == null && nextBooking == null) {
                itemDtosNullIntervals.add(itemDto);
                continue;
            }

            if (lastBooking != null) {
                itemDto.setLastBooking(BookingMapper.toBookingTimeIntervalDto(lastBooking));
            }
            if (nextBooking != null) {
                itemDto.setNextBooking(BookingMapper.toBookingTimeIntervalDto(nextBooking));
            }
            itemDtos.add(itemDto);
        }

        itemDtos.addAll(itemDtosNullIntervals);

        return itemDtos;
    }

    @GetMapping("/search")
    Collection<ItemDto> searchItems(@NotBlank @RequestParam String text) {
        return itemService.searchItems(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
