package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDtoSimpleWithStatus;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestResponseDto {
    Collection<ItemDtoSimpleWithStatus> items;
    private long id;
    @NotBlank(message = "Описание запроса не может быть пустым.")
    @Size(max = 1000, message = "Длина описания запроса не может превышать 1000 символов.")
    private String description;
    private LocalDateTime created;
}
