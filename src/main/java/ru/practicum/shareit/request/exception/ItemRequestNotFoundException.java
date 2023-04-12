package ru.practicum.shareit.request.exception;

public class ItemRequestNotFoundException extends RuntimeException {
    public ItemRequestNotFoundException(long requestId) {
        super(String.format("Запрос c ID=%s не найден.", requestId));
    }
}
