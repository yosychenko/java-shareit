package ru.practicum.shareit.item.exception;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(long itemId) {
        super(String.format("Вещь c ID=%s не найдена.", itemId));
    }
}
