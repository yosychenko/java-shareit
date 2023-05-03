package ru.practicum.shareit.booking.exception;

public class UserHasNoAccessToBookingException extends RuntimeException {
    public UserHasNoAccessToBookingException(long userId, long bookingId) {
        super(String.format(
                "Пользователь с ID=%s не является автором брони с ID=%s или владельцем забронированной вещи.", userId, bookingId)
        );
    }
}
