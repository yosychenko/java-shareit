package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDtoSimple;
import ru.practicum.shareit.user.dto.UserDtoSimple;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemDtoSimple item;
    private UserDtoSimple booker;
    private BookingState status;
}
