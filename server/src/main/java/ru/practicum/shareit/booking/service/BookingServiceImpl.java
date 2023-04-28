package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.exception.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingStorage;

    private final ItemService itemService;

    private final UserService userService;

    @Autowired
    public BookingServiceImpl(
            BookingRepository bookingStorage,
            ItemService itemService,
            UserService userService
    ) {
        this.bookingStorage = bookingStorage;
        this.itemService = itemService;
        this.userService = userService;
    }

    @Override
    @Transactional
    public Booking createBooking(long userId, CreateBookingDto newBooking) {
        Item itemToBook = itemService.getItemById(newBooking.getItemId());
        if (!itemToBook.getAvailable()) {
            throw new CannotBookUnavailableItemException(itemToBook.getId());
        }
        if (itemToBook.getOwner().getId() == userId) {
            throw new CannotBookOwnedItemException(userId, itemToBook.getId());
        }

        User booker = userService.getUserById(userId);

        Booking booking = new Booking();
        booking.setStart(newBooking.getStart());
        booking.setEnd(newBooking.getEnd());
        booking.setItem(itemToBook);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        return bookingStorage.save(booking);
    }

    @Override
    @Transactional
    public Booking approveBooking(long userId, long bookingId, boolean isApproved) {
        User owner = userService.getUserById(userId);
        Booking bookingToApprove = getBookingById(owner.getId(), bookingId);

        if (bookingToApprove.getItem().getOwner().getId() != owner.getId()) {
            throw new CannotApproveBookingException(bookingToApprove.getItem().getId(), owner.getId());
        }
        if ((bookingToApprove.getStatus().equals(BookingStatus.APPROVED) && isApproved) ||
                (bookingToApprove.getStatus().equals(BookingStatus.REJECTED) && !isApproved)) {
            throw new SameApproveStatusException(bookingToApprove.getId(), bookingToApprove.getStatus());
        }

        if (isApproved) {
            bookingToApprove.setStatus(BookingStatus.APPROVED);
        } else {
            bookingToApprove.setStatus(BookingStatus.REJECTED);
        }

        return bookingStorage.save(bookingToApprove);
    }

    @Override
    public Booking getBookingById(long userId, long bookingId) {
        Booking booking = bookingStorage
                .findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new UserHasNoAccessToBookingException(userId, booking.getId());
        }

        return booking;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Booking> getUserBookings(long userId, BookingState state, Pageable pageable) {
        User user = userService.getUserById(userId);

        if (state.equals(BookingState.ALL)) {
            return bookingStorage.findBookingsByBookerOrderByStartDesc(user, pageable);
        }
        if (state.equals(BookingState.FUTURE)) {
            return bookingStorage.findBookingsByBookerAndStartAfterOrderByStartDesc(user, LocalDateTime.now(), pageable);
        }
        if (state.equals(BookingState.PAST)) {
            return bookingStorage.findBookingsByBookerAndEndBeforeOrderByStartDesc(user, LocalDateTime.now(), pageable);
        }
        if (state.equals(BookingState.CURRENT)) {
            return bookingStorage.findBookingsByBookerAndStartBeforeAndEndAfterOrderByStartDesc(user, LocalDateTime.now(), LocalDateTime.now(), pageable);
        }

        return bookingStorage.findBookingsByBookerAndStatusOrderByStartDesc(user, BookingStatus.valueOf(state.toString()), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Booking> getOwnedItemsBookings(long ownerId, BookingState state, Pageable pageable) {
        Collection<Item> items = itemService.getUserItems(ownerId);

        if (state.equals(BookingState.ALL)) {
            return bookingStorage.findBookingsByItemInOrderByStartDesc(items, pageable);
        }
        if (state.equals(BookingState.FUTURE)) {
            return bookingStorage.findBookingsByItemInAndStartAfterOrderByStartDesc(items, LocalDateTime.now(), pageable);
        }
        if (state.equals(BookingState.PAST)) {
            return bookingStorage.findBookingsByItemInAndEndBeforeOrderByStartDesc(items, LocalDateTime.now(), pageable);
        }
        if (state.equals(BookingState.CURRENT)) {
            return bookingStorage.findBookingsByItemInAndStartBeforeAndEndAfterOrderByStartDesc(items, LocalDateTime.now(), LocalDateTime.now(), pageable);
        }

        return bookingStorage.findBookingsByItemInAndStatusOrderByStartDesc(items, BookingStatus.valueOf(state.toString()), pageable);
    }
}
