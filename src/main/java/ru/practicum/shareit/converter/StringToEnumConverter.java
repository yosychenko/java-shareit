package ru.practicum.shareit.converter;


import org.springframework.core.convert.converter.Converter;
import ru.practicum.shareit.booking.exception.IncorrectBookingStatusException;
import ru.practicum.shareit.booking.model.BookingStatus;

public class StringToEnumConverter implements Converter<String, BookingStatus> {
    @Override
    public BookingStatus convert(String source) {
        try {
            return BookingStatus.valueOf(source);
        } catch (IllegalArgumentException e) {
            throw new IncorrectBookingStatusException(source);
        }
    }
}
