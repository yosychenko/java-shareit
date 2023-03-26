package ru.practicum.shareit.booking.exception;

public class BookingPeriodIsNotValidException extends RuntimeException {
    public BookingPeriodIsNotValidException() {
        super(String.format("У бронирования задан некорректный период бронирования."));
    }
}
