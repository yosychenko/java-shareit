package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDtoSimple;
import ru.practicum.shareit.user.dto.UserDtoSimple;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        ItemDtoSimple itemDto = ItemDtoSimple.builder()
                .id(booking.getItem().getId())
                .name(booking.getItem().getName())
                .build();
        UserDtoSimple userDto = UserDtoSimple.builder()
                .id(booking.getBooker().getId())
                .name(booking.getBooker().getName())
                .build();
        BookingStatus status = BookingStatus.valueOf(booking.getStatus().name());

        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(itemDto)
                .booker(userDto)
                .status(status)
                .build();
    }

    public static BookingTimeIntervalDto toBookingTimeIntervalDto(Booking booking) {
        return BookingTimeIntervalDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .bookerId(booking.getBooker().getId())
                .build();
    }
}
