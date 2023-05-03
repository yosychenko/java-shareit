package ru.practicum.shareit.pagination;

public class PageableValidator {
    public static boolean isValid(int from, int size) {
        return from >= 0 && size > 0;
    }
}
