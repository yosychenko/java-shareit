package ru.practicum.shareit.user.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(long userId) {
        super(String.format("Пользователь c ID=%s не найден.", userId));
    }
}

