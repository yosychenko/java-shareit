package ru.practicum.shareit.booking.exception;

public class CannotBookUnavailableItemException extends RuntimeException {
    public CannotBookUnavailableItemException(long itemId) {
        super(String.format("Вещь с ID=%s недоступна для бронирования.", itemId));
    }
}
