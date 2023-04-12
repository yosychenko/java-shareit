package ru.practicum.shareit.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.PageableIsNotValidException;
import ru.practicum.shareit.pagination.PageableAdjuster;
import ru.practicum.shareit.pagination.PageableValidator;

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
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long bookingId,
            @RequestParam boolean approved
    ) {
        Booking updatedBooking = bookingService.approveBooking(userId, bookingId, approved);
        return BookingMapper.toBookingDto(updatedBooking);
    }

    @GetMapping("/{bookingId}")
    BookingDto getBookingById(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long bookingId
    ) {
        Booking booking = bookingService.getBookingById(userId, bookingId);
        return BookingMapper.toBookingDto(booking);
    }

    @GetMapping
    Collection<BookingDto> getUserBookings(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "ALL") BookingState state,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "2000") int size
    ) {
        if (!PageableValidator.isValid(from, size)) {
            throw new PageableIsNotValidException();
        }

        int newFrom = PageableAdjuster.adjustFrom(from, size);

        return bookingService.getUserBookings(userId, state, PageRequest.of(newFrom, size)).stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    Collection<BookingDto> getOwnedItemsBookings(
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @RequestParam(defaultValue = "ALL") BookingState state,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "2000") int size
    ) {
        if (!PageableValidator.isValid(from, size)) {
            throw new PageableIsNotValidException();
        }

        int newFrom = PageableAdjuster.adjustFrom(from, size);

        return bookingService.getOwnedItemsBookings(ownerId, state, PageRequest.of(newFrom, size)).stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}
