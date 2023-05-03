package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDtoSimpleWithStatus;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestResponseDto {
    Collection<ItemDtoSimpleWithStatus> items;
    private long id;
    private String description;
    private LocalDateTime created;
}
