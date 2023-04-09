package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class CreateBookingDto {
    @NotNull(message = "Идентификатор вещи для брони не может быть пустым.")
    private Long itemId;
    @NotNull(message = "Дата начала брони не может быть пустой.")
    private LocalDateTime start;
    @NotNull(message = "Дата окончания брони не может быть пустой.")
    private LocalDateTime end;
}
