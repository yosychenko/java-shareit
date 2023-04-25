package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingTimeIntervalDto implements Comparable<BookingTimeIntervalDto> {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private long bookerId;

    @Override
    public int compareTo(BookingTimeIntervalDto o) {
        return start.compareTo(o.getStart());
    }
}
