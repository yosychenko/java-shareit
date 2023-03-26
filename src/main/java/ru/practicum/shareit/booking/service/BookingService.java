package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface BookingService {
    Booking createBooking(long userId, CreateBookingDto newBooking);

    Booking approveBooking(long bookingId, long userId, boolean isApproved);

    Booking getBookingById(long bookingId, long userId);

    Collection<Booking> getUserBookings(long userId, BookingStatus bookingStatus);

    Collection<Booking> getOwnedItemsBookings(long ownerId, BookingStatus bookingStatus);

    Booking getLastItemBooking(Item item);

    Booking getNextItemBooking(Item item);
}
