package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookingDto {
    @NotNull(message = "Идентификатор вещи для брони не может быть пустым.")
    private Long itemId;
    @NotNull(message = "Дата начала брони не может быть пустой.")
    private LocalDateTime start;
    @NotNull(message = "Дата окончания брони не может быть пустой.")
    private LocalDateTime end;
}
