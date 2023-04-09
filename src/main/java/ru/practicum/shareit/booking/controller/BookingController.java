package ru.practicum.shareit.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    BookingDto createBooking(
            @RequestHeader(value = "X-Sharer-User-Id") long userId,
            @Valid @RequestBody CreateBookingDto newBookingDto
    ) {
        Booking createdBooking = bookingService.createBooking(userId, newBookingDto);
        return BookingMapper.toBookingDto(createdBooking);
    }

    @PatchMapping("/{bookingId}")
    BookingDto approveBooking(
            @PathVariable long bookingId,
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam boolean approved
    ) {
        Booking updatedBooking = bookingService.approveBooking(bookingId, userId, approved);
        return BookingMapper.toBookingDto(updatedBooking);
    }

    @GetMapping("/{bookingId}")
    BookingDto getBookingById(
            @PathVariable long bookingId,
            @RequestHeader("X-Sharer-User-Id") long userId
    ) {
        Booking booking = bookingService.getBookingById(bookingId, userId);
        return BookingMapper.toBookingDto(booking);
    }

    @GetMapping
    Collection<BookingDto> getUserBookings(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "ALL") BookingState state
    ) {

        return bookingService.getUserBookings(userId, state).stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    Collection<BookingDto> getOwnedItemsBookings(
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @RequestParam(defaultValue = "ALL") BookingState state
    ) {
        return bookingService.getOwnedItemsBookings(ownerId, state).stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}
