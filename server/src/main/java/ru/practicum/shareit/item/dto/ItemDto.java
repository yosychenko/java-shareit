package ru.practicum.shareit.item.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingTimeIntervalDto;

import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingTimeIntervalDto lastBooking;
    private BookingTimeIntervalDto nextBooking;
    private Collection<CommentDto> comments;
    private Long requestId;
}
