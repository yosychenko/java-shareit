package ru.practicum.shareit.pagination;

public class PageableAdjuster {
    public static int adjustFrom(int from, int size) {
        if (from > size) {
            return from - size;
        }
        return from;
    }
}
