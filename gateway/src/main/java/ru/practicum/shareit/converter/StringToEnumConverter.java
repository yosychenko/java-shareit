package ru.practicum.shareit.converter;


import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.exception.IncorrectBookingStatusException;

@Component
public class StringToEnumConverter implements Converter<String, BookingState> {
    @Override
    public BookingState convert(String source) {
        try {
            return BookingState.valueOf(source);
        } catch (IllegalArgumentException e) {
            throw new IncorrectBookingStatusException(source);
        }
    }
}
