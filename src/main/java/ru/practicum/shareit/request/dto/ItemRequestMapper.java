package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDtoSimple;

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
                .build();
    }

    public static ItemRequest fromItemRequestDto(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();

        itemRequest.setDescription(itemRequestDto.getDescription());

        return itemRequest;
    }
}
