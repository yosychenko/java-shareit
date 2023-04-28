package ru.practicum.shareit.converter;


import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingState;

@Component
public class StringToEnumConverter implements Converter<String, BookingState> {
    @Override
    public BookingState convert(String source) {
        return BookingState.valueOf(source);
    }
}
