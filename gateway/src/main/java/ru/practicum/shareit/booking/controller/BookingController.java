package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.exception.BookingPeriodIsNotValidException;
import ru.practicum.shareit.exception.PageableIsNotValidException;
import ru.practicum.shareit.pagination.PageableAdjuster;
import ru.practicum.shareit.pagination.PageableValidator;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(
            @RequestHeader(value = "X-Sharer-User-Id") long userId,
            @Valid @RequestBody CreateBookingDto newBookingDto
    ) {
        if (!isBookingPeriodValid(newBookingDto)) {
            throw new BookingPeriodIsNotValidException();
        }

        return bookingClient.createBooking(userId, newBookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long bookingId,
            @RequestParam boolean approved
    ) {
        return bookingClient.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long bookingId
    ) {
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookings(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "ALL") BookingState state,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "2000") int size
    ) {
        if (!PageableValidator.isValid(from, size)) {
            throw new PageableIsNotValidException();
        }

        int newFrom = PageableAdjuster.adjustFrom(from, size);

        return bookingClient.getUserBookings(userId, state, newFrom, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnedItemsBookings(
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @RequestParam(defaultValue = "ALL") BookingState state,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "2000") int size
    ) {
        if (!PageableValidator.isValid(from, size)) {
            throw new PageableIsNotValidException();
        }

        int newFrom = PageableAdjuster.adjustFrom(from, size);

        return bookingClient.getOwnedItemsBookings(ownerId, state, newFrom, size);
    }

    private boolean isBookingPeriodValid(CreateBookingDto bookingDto) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();

        return !start.equals(end) &&
                !start.isAfter(end) &&
                !end.isBefore(start) &&
                !end.isBefore(now) &&
                !start.isBefore(now);
    }
}
