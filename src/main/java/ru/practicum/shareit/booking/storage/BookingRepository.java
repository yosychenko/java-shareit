package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Collection<Booking> findBookingsByBookerOrderByStartDesc(User user);

    Collection<Booking> findBookingsByBookerAndStatusOrderByStartDesc(User user, BookingStatus bookingStatus);

    Collection<Booking> findBookingsByBookerAndStartAfterOrderByStartDesc(User user, LocalDateTime currentTime);

    Collection<Booking> findBookingsByBookerAndEndBeforeOrderByStartDesc(User user, LocalDateTime currentTime);

    Collection<Booking> findBookingsByBookerAndStartBeforeAndEndAfterOrderByStartDesc(User user, LocalDateTime currentTime1, LocalDateTime currentTime2);

    Collection<Booking> findBookingsByItemInOrderByStartDesc(Collection<Item> items);

    Collection<Booking> findBookingsByItemInAndStatusOrderByStartDesc(Collection<Item> items, BookingStatus bookingStatus);

    Collection<Booking> findBookingsByItemInAndStartAfterOrderByStartDesc(Collection<Item> items, LocalDateTime currentTime);

    Collection<Booking> findBookingsByItemInAndEndBeforeOrderByStartDesc(Collection<Item> items, LocalDateTime currentTime);

    Collection<Booking> findBookingsByItemInAndStartBeforeAndEndAfterOrderByStartDesc(Collection<Item> items, LocalDateTime currentTime1, LocalDateTime currentTime2);

    @Query(value = "SELECT * FROM bookings WHERE item = ?1 AND (end_ts < CURRENT_TIMESTAMP OR (start_ts < CURRENT_TIMESTAMP AND end_ts > CURRENT_TIMESTAMP)) AND status != 'REJECTED' ORDER BY end_ts DESC LIMIT 1", nativeQuery = true)
    Booking getLastItemBooking(Item item);

    @Query(value = "SELECT * FROM bookings WHERE item = ?1 AND start_ts > CURRENT_TIMESTAMP AND status != 'REJECTED' ORDER BY start_ts LIMIT 1", nativeQuery = true)
    Booking getNextItemBooking(Item item);

    Collection<Booking> getBookingsByBookerAndItemAndEndIsBeforeAndStatus(User user, Item item, LocalDateTime currentTime, BookingStatus bookingStatus);
}
