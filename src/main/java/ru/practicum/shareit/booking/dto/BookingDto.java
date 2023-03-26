package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    private long id;
    @NotNull(message = "Дата начала брони не может быть пустой")
    private LocalDateTime start;
    @NotNull(message = "Дата окончания брони не может быть пустой")
    private LocalDateTime end;
    private Item item;
    private User booker;
    private BookingStatus status;
}
