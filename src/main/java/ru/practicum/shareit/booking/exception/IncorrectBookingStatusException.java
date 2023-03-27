package ru.practicum.shareit.booking.exception;

public class IncorrectBookingStatusException extends RuntimeException {
    public IncorrectBookingStatusException(String status) {
        super(String.format("Unknown state: %s", status));
    }
}
