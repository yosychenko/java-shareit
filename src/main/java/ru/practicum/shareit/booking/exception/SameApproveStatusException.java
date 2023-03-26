package ru.practicum.shareit.booking.exception;

import ru.practicum.shareit.booking.model.BookingStatus;

public class SameApproveStatusException extends RuntimeException {
    public SameApproveStatusException(long bookingId, BookingStatus bookingStatus) {
        super(String.format("Бронирование c ID=%s уже имеет статус %s, выберите другой статус.", bookingId, bookingStatus.toString()));
    }
}
