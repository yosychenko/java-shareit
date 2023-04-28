package ru.practicum.shareit.exception;

public class PageableIsNotValidException extends RuntimeException {
    public PageableIsNotValidException() {
        super("Параметры пагинации некорректны.");
    }
}
