package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    public Booking createBooking(long userId, CreateBookingDto newBooking) {
        if (!isBookingPeriodValid(newBooking)) {
            throw new BookingPeriodIsNotValidException();
        }

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
    public Booking approveBooking(long bookingId, long userId, boolean isApproved) {
        Booking bookingToApprove = getBookingById(bookingId, userId);

        if (bookingToApprove.getItem().getOwner().getId() != userId) {
            throw new CannotApproveBookingException(bookingToApprove.getItem().getId(), userId);
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
    public Booking getBookingById(long bookingId, long userId) {
        Booking booking = bookingStorage
                .findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new UserHasNoAccessToBookingException(userId, booking.getId());
        }

        return booking;
    }

    @Override
    public Collection<Booking> getUserBookings(long userId, BookingStatus bookingStatus) {
        User user = userService.getUserById(userId);

        if (bookingStatus.equals(BookingStatus.ALL)) {
            return bookingStorage.findBookingsByBookerOrderByStartDesc(user);
        }
        if (bookingStatus.equals(BookingStatus.FUTURE)) {
            return bookingStorage.findBookingsByBookerAndStartAfterOrderByStartDesc(user, LocalDateTime.now());
        }

        return bookingStorage.findBookingsByBookerAndStatusOrderByStartDesc(user, bookingStatus);
    }

    @Override
    public Collection<Booking> getOwnedItemsBookings(long ownerId, BookingStatus bookingStatus) {
        Collection<Item> items = itemService.getUserItems(ownerId);

        if (bookingStatus.equals(BookingStatus.ALL)) {
            return bookingStorage.findBookingsByItemInOrderByStartDesc(items);
        }
        if (bookingStatus.equals(BookingStatus.FUTURE)) {
            return bookingStorage.findBookingsByItemInAndStartAfterOrderByStartDesc(items, LocalDateTime.now());
        }

        return bookingStorage.findBookingsByItemInAndStatusOrderByStartDesc(items, bookingStatus);
    }

    @Override
    public Booking getLastItemBooking(Item item) {
        return bookingStorage.getLastItemBooking(item);
    }

    @Override
    public Booking getNextItemBooking(Item item) {
        return bookingStorage.findFirstByItemAndStartAfterOrderByStart(item, LocalDateTime.now());
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
