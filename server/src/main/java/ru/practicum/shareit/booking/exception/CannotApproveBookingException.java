package ru.practicum.shareit.booking.exception;

public class CannotApproveBookingException extends RuntimeException {
    public CannotApproveBookingException(long itemId, long userId) {
        super(String.format("Нельзя изменить статус бронирования для вещи с ID=%s - " +
                "пользователь с ID=%s не является ее владельцем.", itemId, userId));
    }
}
