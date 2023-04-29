package ru.practicum.shareit.booking.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.ExceptionControllerAdvice;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.TestUtils;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.item.dto.ItemDtoSimple;
import ru.practicum.shareit.user.dto.UserDtoSimple;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ContextConfiguration(classes = ShareItGateway.class)
@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {
    @Mock
    private BookingClient bookingClient;

    @InjectMocks
    private BookingController bookingController;

    private MockMvc mvc;

    private CreateBookingDto createBookingDto;

    private BookingDto bookingDto;

    private ResponseEntity<Object> bookingResponse;

    @BeforeEach
    void beforeEach() throws Exception {
        mvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .setControllerAdvice(ExceptionControllerAdvice.class)
                .build();

        createBookingDto = CreateBookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
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
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(itemDtoSimple)
                .booker(userDtoSimple)
                .status(BookingState.WAITING)
                .build();

        bookingResponse = new ResponseEntity<>(
                TestUtils.asJsonString(bookingDto),
                HttpStatus.OK
        );
    }

    @Test
    void testCreateBooking() throws Exception {
        when(bookingClient.createBooking(anyLong(), any(CreateBookingDto.class))).thenReturn(bookingResponse);

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
        when(bookingClient.approveBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingResponse);

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
        when(bookingClient.getBookingById(anyLong(), anyLong())).thenReturn(bookingResponse);

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
        bookingResponse = new ResponseEntity<>(
                TestUtils.asJsonString(Map.of("message", "Бронирование c ID=1 не найдено.")),
                HttpStatus.NOT_FOUND
        );

        when(bookingClient.getBookingById(anyLong(), anyLong())).thenReturn(bookingResponse);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("message")))
                .andExpect(jsonPath("$.message", is("Бронирование c ID=1 не найдено.")));
    }

    @Test
    void testGetUserBookings() throws Exception {
        bookingResponse = new ResponseEntity<>(
                TestUtils.asJsonString(List.of(bookingDto)),
                HttpStatus.OK
        );


        when(bookingClient.getUserBookings(anyLong(), any(BookingState.class), anyInt(), anyInt())).thenReturn(bookingResponse);

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
        bookingResponse = new ResponseEntity<>(
                TestUtils.asJsonString(Map.of("message", "Пользователь c ID=1 не найден.")),
                HttpStatus.NOT_FOUND
        );

        when(bookingClient.getUserBookings(anyLong(), any(BookingState.class), anyInt(), anyInt())).thenReturn(bookingResponse);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("message")))
                .andExpect(jsonPath("$.message", is("Пользователь c ID=1 не найден.")));
    }

    @Test
    void testGetOwnedItemsBookings() throws Exception {
        bookingResponse = new ResponseEntity<>(
                TestUtils.asJsonString(List.of(bookingDto)),
                HttpStatus.OK
        );

        when(bookingClient.getOwnedItemsBookings(anyLong(), any(BookingState.class), anyInt(), anyInt())).thenReturn(bookingResponse);

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
