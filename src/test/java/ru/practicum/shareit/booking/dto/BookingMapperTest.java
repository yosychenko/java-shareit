package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDtoSimple;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDtoSimple;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class BookingMapperTest {

    private static User owner;

    private static User booker;

    private static Item item;

    private static Booking booking;

    @BeforeAll
    static void beforeAll() {

        owner = new User();
        owner.setId(1L);
        owner.setName("John");
        owner.setEmail("john.doe@mail.com");

        booker = new User();
        booker.setId(1L);
        booker.setName("Booker");
        booker.setEmail("john.booker@mail.com");

        item = new Item();
        item.setId(1L);
        item.setName("item_name");
        item.setDescription("item_description");
        item.setAvailable(true);
        item.setOwner(owner);

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().minusDays(7));
        booking.setEnd(LocalDateTime.now().minusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingState.WAITING);
    }

    @Test
    void testToBookingDto() {
        BookingDto expetedBookingDto = BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(
                        ItemDtoSimple.builder()
                                .id(item.getId())
                                .name(item.getName())
                                .build()
                )
                .booker(
                        UserDtoSimple.builder()
                                .id(booker.getId())
                                .name(booker.getName())
                                .build()
                )
                .status(BookingStatus.valueOf(booking.getStatus().name()))
                .build();

        BookingDto resultDto = BookingMapper.toBookingDto(booking);

        assertThat(resultDto).isNotNull();
        assertThat(resultDto.getId()).isEqualTo(expetedBookingDto.getId());
        assertThat(resultDto.getStart()).isEqualTo(expetedBookingDto.getStart());
        assertThat(resultDto.getEnd()).isEqualTo(expetedBookingDto.getEnd());
        assertThat(resultDto.getItem().getId()).isEqualTo(expetedBookingDto.getItem().getId());
        assertThat(resultDto.getItem().getName()).isEqualTo(expetedBookingDto.getItem().getName());
        assertThat(resultDto.getBooker().getId()).isEqualTo(expetedBookingDto.getBooker().getId());
        assertThat(resultDto.getBooker().getName()).isEqualTo(expetedBookingDto.getBooker().getName());
        assertThat(resultDto.getStatus()).isEqualTo(expetedBookingDto.getStatus());
    }

    @Test
    void toBookingTimeIntervalDto() {
        BookingTimeIntervalDto expectedBookingDto = BookingTimeIntervalDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .bookerId(booking.getBooker().getId())
                .build();

        BookingTimeIntervalDto resultDto = BookingMapper.toBookingTimeIntervalDto(booking);

        assertThat(resultDto).isNotNull();
        assertThat(resultDto.getId()).isEqualTo(expectedBookingDto.getId());
        assertThat(resultDto.getStart()).isEqualTo(expectedBookingDto.getStart());
        assertThat(resultDto.getEnd()).isEqualTo(expectedBookingDto.getEnd());
        assertThat(resultDto.getBookerId()).isEqualTo(expectedBookingDto.getBookerId());
    }

}
