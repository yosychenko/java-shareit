package ru.practicum.shareit.booking.exception;

import ru.practicum.shareit.booking.model.BookingState;

public class SameApproveStatusException extends RuntimeException {
    public SameApproveStatusException(long bookingId, BookingState bookingState) {
        super(String.format("Бронирование c ID=%s уже имеет статус %s, выберите другой статус.", bookingId, bookingState.toString()));
    }
}
