package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemDtoSimpleWithStatus;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDtoSimple;

import java.util.Collection;
import java.util.stream.Collectors;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestor(
                        UserDtoSimple.builder()
                                .id(itemRequest.getRequestor().getId())
                                .name(itemRequest.getRequestor().getName())
                                .build()
                )
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequestResponseDto toItemRequestResponseDto(ItemRequest itemRequest, Collection<Item> items) {
        Collection<ItemDtoSimpleWithStatus> itemDtos = items.stream()
                .map(ItemMapper::toItemDtoSimpleWithStatus)
                .collect(Collectors.toList());

        return ItemRequestResponseDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(itemDtos)
                .build();
    }

    public static ItemRequest fromItemRequestDto(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());

        return itemRequest;
    }
}
