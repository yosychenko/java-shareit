package ru.practicum.shareit.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

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
    public BookingDto createBooking(
            @RequestHeader(value = "X-Sharer-User-Id") long userId,
            @RequestBody CreateBookingDto newBookingDto
    ) {
        Booking createdBooking = bookingService.createBooking(userId, newBookingDto);
        return BookingMapper.toBookingDto(createdBooking);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long bookingId,
            @RequestParam boolean approved
    ) {
        Booking updatedBooking = bookingService.approveBooking(userId, bookingId, approved);
        return BookingMapper.toBookingDto(updatedBooking);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long bookingId
    ) {
        Booking booking = bookingService.getBookingById(userId, bookingId);
        return BookingMapper.toBookingDto(booking);
    }

    @GetMapping
    public Collection<BookingDto> getUserBookings(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "ALL") BookingState state,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "2000") int size
    ) {
        return bookingService.getUserBookings(userId, state, PageRequest.of(from, size)).stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getOwnedItemsBookings(
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @RequestParam(defaultValue = "ALL") BookingState state,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "2000") int size
    ) {

        return bookingService.getOwnedItemsBookings(ownerId, state, PageRequest.of(from, size)).stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}
