package ru.practicum.shareit.item.dto;


import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingTimeIntervalDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;

@Data
@Builder
public class ItemDto {
    private long id;
    @NotBlank(message = "Название вещи не может быть пустым.")
    @Size(max = 255, message = "Длина названия вещи не может превышать 255 символов.")
    private String name;
    @NotBlank(message = "Описание вещи не может быть пустым.")
    @Size(max = 1000, message = "Длина описания вещи не может превышать 1000 символов.")
    private String description;
    @NotNull(message = "Статус доступности вещи должен быть указан")
    private Boolean available;
    private BookingTimeIntervalDto lastBooking;
    private BookingTimeIntervalDto nextBooking;
    private Collection<CommentDto> comments;
    private Long requestId;
}
