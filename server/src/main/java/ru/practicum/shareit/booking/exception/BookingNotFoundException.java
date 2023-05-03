package ru.practicum.shareit.booking.exception;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(long bookingId) {
        super(String.format("Бронирование c ID=%s не найдено.", bookingId));
    }
}
