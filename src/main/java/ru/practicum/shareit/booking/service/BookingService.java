package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.Collection;

public interface BookingService {
    Booking createBooking(long userId, CreateBookingDto newBooking);

    Booking approveBooking(long bookingId, long userId, boolean isApproved);

    Booking getBookingById(long bookingId, long userId);

    Collection<Booking> getUserBookings(long userId, BookingState state);

    Collection<Booking> getOwnedItemsBookings(long ownerId, BookingState state);
}
