package ru.practicum.shareit.item.exception;

public class CannotLeaveCommentException extends RuntimeException {
    public CannotLeaveCommentException(long userId, long itemId) {
        super(String.format("Пользователь с ID=%s не может оставить комментарий к вещи с ID=%s", userId, itemId));
    }
}
