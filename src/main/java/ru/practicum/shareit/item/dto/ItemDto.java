package ru.practicum.shareit.item.dto;


import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingTimeIntervalDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Data
@Builder
public class ItemDto {
    private long id;
    @NotBlank(message = "Название вещи не может быть пустым.")
    private String name;
    @NotBlank(message = "Описание вещи не может быть пустым.")
    private String description;
    @NotNull(message = "Статус доступности вещи должен быть указан")
    private Boolean available;
    private BookingTimeIntervalDto lastBooking;
    private BookingTimeIntervalDto nextBooking;
    private Collection<CommentDto> comments;
}
