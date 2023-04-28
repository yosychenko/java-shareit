package ru.practicum.shareit.booking.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.ExceptionControllerAdvice;
import ru.practicum.shareit.TestUtils;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.item.dto.ItemDtoSimple;
import ru.practicum.shareit.user.dto.UserDtoSimple;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {
    @Mock
    private BookingClient bookingClient;

    @InjectMocks
    private BookingController bookingController;

    private MockMvc mvc;

    private CreateBookingDto createBookingDto;

    private BookingDto bookingDto;

    @BeforeEach
    void beforeEach() throws Exception {
        mvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .setControllerAdvice(ExceptionControllerAdvice.class)
                .build();

        createBookingDto = CreateBookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2023, 4, 13, 10, 0))
                .end(LocalDateTime.of(2023, 4, 20, 10, 0))
                .build();

        ItemDtoSimple itemDtoSimple = ItemDtoSimple.builder()
                .id(10L)
                .name("item_name")
                .build();
        UserDtoSimple userDtoSimple = UserDtoSimple.builder()
                .id(23L)
                .name("user_name")
                .build();

        bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 4, 13, 10, 0))
                .end(LocalDateTime.of(2023, 4, 20, 10, 0))
                .item(itemDtoSimple)
                .booker(userDtoSimple)
                .status(BookingStatus.WAITING)
                .build();

        Item item = new Item();
        item.setId(10L);
        item.setName("item_name");

        User user = new User();
        user.setId(23L);
        user.setName("user_name");

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.of(2023, 4, 13, 10, 0));
        booking.setEnd(LocalDateTime.of(2023, 4, 20, 10, 0));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingState.WAITING);

    }

    @Test
    void testCreateBooking() throws Exception {
        when(bookingClient.createBooking(anyLong(), any(CreateBookingDto.class))).thenReturn(booking);

        mvc.perform(post("/bookings")
                        .content(TestUtils.asJsonString(createBookingDto))
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start[0]", is(bookingDto.getStart().getYear())))
                .andExpect(jsonPath("$.start[1]", is(bookingDto.getStart().getMonthValue())))
                .andExpect(jsonPath("$.start[2]", is(bookingDto.getStart().getDayOfMonth())))
                .andExpect(jsonPath("$.start[3]", is(bookingDto.getStart().getHour())))
                .andExpect(jsonPath("$.start[4]", is(bookingDto.getStart().getMinute())))
                .andExpect(jsonPath("$.end[0]", is(bookingDto.getEnd().getYear())))
                .andExpect(jsonPath("$.end[1]", is(bookingDto.getEnd().getMonthValue())))
                .andExpect(jsonPath("$.end[2]", is(bookingDto.getEnd().getDayOfMonth())))
                .andExpect(jsonPath("$.end[3]", is(bookingDto.getEnd().getHour())))
                .andExpect(jsonPath("$.end[4]", is(bookingDto.getEnd().getMinute())))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(bookingDto.getBooker().getName())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void testApproveBooking() throws Exception {
        when(bookingClient.approveBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(booking);

        mvc.perform(patch("/bookings/1?approved=true")
                        .content(TestUtils.asJsonString(createBookingDto))
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start[0]", is(bookingDto.getStart().getYear())))
                .andExpect(jsonPath("$.start[1]", is(bookingDto.getStart().getMonthValue())))
                .andExpect(jsonPath("$.start[2]", is(bookingDto.getStart().getDayOfMonth())))
                .andExpect(jsonPath("$.start[3]", is(bookingDto.getStart().getHour())))
                .andExpect(jsonPath("$.start[4]", is(bookingDto.getStart().getMinute())))
                .andExpect(jsonPath("$.end[0]", is(bookingDto.getEnd().getYear())))
                .andExpect(jsonPath("$.end[1]", is(bookingDto.getEnd().getMonthValue())))
                .andExpect(jsonPath("$.end[2]", is(bookingDto.getEnd().getDayOfMonth())))
                .andExpect(jsonPath("$.end[3]", is(bookingDto.getEnd().getHour())))
                .andExpect(jsonPath("$.end[4]", is(bookingDto.getEnd().getMinute())))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(bookingDto.getBooker().getName())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void testGetBookingById() throws Exception {
        when(bookingClient.getBookingById(anyLong(), anyLong())).thenReturn(booking);


        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start[0]", is(bookingDto.getStart().getYear())))
                .andExpect(jsonPath("$.start[1]", is(bookingDto.getStart().getMonthValue())))
                .andExpect(jsonPath("$.start[2]", is(bookingDto.getStart().getDayOfMonth())))
                .andExpect(jsonPath("$.start[3]", is(bookingDto.getStart().getHour())))
                .andExpect(jsonPath("$.start[4]", is(bookingDto.getStart().getMinute())))
                .andExpect(jsonPath("$.end[0]", is(bookingDto.getEnd().getYear())))
                .andExpect(jsonPath("$.end[1]", is(bookingDto.getEnd().getMonthValue())))
                .andExpect(jsonPath("$.end[2]", is(bookingDto.getEnd().getDayOfMonth())))
                .andExpect(jsonPath("$.end[3]", is(bookingDto.getEnd().getHour())))
                .andExpect(jsonPath("$.end[4]", is(bookingDto.getEnd().getMinute())))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(bookingDto.getBooker().getName())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void testGetBookingByIdNonExistentId() throws Exception {
        when(bookingClient.getBookingById(anyLong(), anyLong())).thenThrow(new BookingNotFoundException(1L));

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("message")))
                .andExpect(jsonPath("$.message", is("Бронирование c ID=1 не найдено.")));
    }

    @Test
    void testGetUserBookings() throws Exception {
        when(bookingClient.getUserBookings(anyLong(), any(BookingState.class), any(Pageable.class))).thenReturn(List.of(booking));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start[0]", is(bookingDto.getStart().getYear())))
                .andExpect(jsonPath("$[0].start[1]", is(bookingDto.getStart().getMonthValue())))
                .andExpect(jsonPath("$[0].start[2]", is(bookingDto.getStart().getDayOfMonth())))
                .andExpect(jsonPath("$[0].start[3]", is(bookingDto.getStart().getHour())))
                .andExpect(jsonPath("$[0].start[4]", is(bookingDto.getStart().getMinute())))
                .andExpect(jsonPath("$[0].end[0]", is(bookingDto.getEnd().getYear())))
                .andExpect(jsonPath("$[0].end[1]", is(bookingDto.getEnd().getMonthValue())))
                .andExpect(jsonPath("$[0].end[2]", is(bookingDto.getEnd().getDayOfMonth())))
                .andExpect(jsonPath("$[0].end[3]", is(bookingDto.getEnd().getHour())))
                .andExpect(jsonPath("$[0].end[4]", is(bookingDto.getEnd().getMinute())))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.name", is(bookingDto.getBooker().getName())))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())));

    }

    @Test
    void testGetUserBookingsNonExistentUser() throws Exception {
        when(bookingClient.getUserBookings(anyLong(), any(BookingState.class), any(Pageable.class))).thenThrow(new UserNotFoundException(1L));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("message")))
                .andExpect(jsonPath("$.message", is("Пользователь c ID=1 не найден.")));
    }

    @Test
    void testGetOwnedItemsBookings() throws Exception {
        when(bookingClient.getOwnedItemsBookings(anyLong(), any(BookingState.class), any(Pageable.class))).thenReturn(List.of(booking));


        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start[0]", is(bookingDto.getStart().getYear())))
                .andExpect(jsonPath("$[0].start[1]", is(bookingDto.getStart().getMonthValue())))
                .andExpect(jsonPath("$[0].start[2]", is(bookingDto.getStart().getDayOfMonth())))
                .andExpect(jsonPath("$[0].start[3]", is(bookingDto.getStart().getHour())))
                .andExpect(jsonPath("$[0].start[4]", is(bookingDto.getStart().getMinute())))
                .andExpect(jsonPath("$[0].end[0]", is(bookingDto.getEnd().getYear())))
                .andExpect(jsonPath("$[0].end[1]", is(bookingDto.getEnd().getMonthValue())))
                .andExpect(jsonPath("$[0].end[2]", is(bookingDto.getEnd().getDayOfMonth())))
                .andExpect(jsonPath("$[0].end[3]", is(bookingDto.getEnd().getHour())))
                .andExpect(jsonPath("$[0].end[4]", is(bookingDto.getEnd().getMinute())))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.name", is(bookingDto.getBooker().getName())))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())));
    }
}
