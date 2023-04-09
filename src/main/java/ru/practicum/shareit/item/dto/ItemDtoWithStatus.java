package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

@Data
@Builder
public class ItemDtoWithStatus {
    private long id;
    private String name;
    private Boolean available;
    private long requestId;
}
