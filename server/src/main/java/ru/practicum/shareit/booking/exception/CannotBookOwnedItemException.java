package ru.practicum.shareit.booking.exception;

public class CannotBookOwnedItemException extends RuntimeException {
    public CannotBookOwnedItemException(long userId, long itemId) {
        super(String.format("Пользователь с ID=%s не может забронировать свою вещь с ID=%s.", userId, itemId));
    }
}
