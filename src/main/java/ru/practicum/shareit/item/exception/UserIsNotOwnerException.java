package ru.practicum.shareit.item.exception;

public class UserIsNotOwnerException extends RuntimeException {
    public UserIsNotOwnerException(long userId, long itemId) {
        super(String.format("Нельзя обновить вещь. Пользователь c ID=%d не является владельцем вещи с ID=%d.", userId, itemId));
    }
}
