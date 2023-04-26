package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDtoSimple;
import ru.practicum.shareit.user.dto.UserDtoSimple;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    private long id;
    @NotNull(message = "Дата начала брони не может быть пустой.")
    private LocalDateTime start;
    @NotNull(message = "Дата окончания брони не может быть пустой.")
    private LocalDateTime end;
    private ItemDtoSimple item;
    private UserDtoSimple booker;
    private BookingStatus status;
}
