package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Collection<Booking> findBookingsByBookerOrderByStartDesc(User user);

    Collection<Booking> findBookingsByBookerAndStatusOrderByStartDesc(User user, BookingState state);

    Collection<Booking> findBookingsByBookerAndStartAfterOrderByStartDesc(User user, LocalDateTime currentTime);

    Collection<Booking> findBookingsByBookerAndEndBeforeOrderByStartDesc(User user, LocalDateTime currentTime);

    Collection<Booking> findBookingsByBookerAndStartBeforeAndEndAfterOrderByStartDesc(User user, LocalDateTime currentTime1, LocalDateTime currentTime2);

    Collection<Booking> findBookingsByItemInOrderByStartDesc(Collection<Item> items);

    Collection<Booking> findBookingsByItemInAndStatusOrderByStartDesc(Collection<Item> items, BookingState state);

    Collection<Booking> findBookingsByItemInAndStartAfterOrderByStartDesc(Collection<Item> items, LocalDateTime currentTime);

    Collection<Booking> findBookingsByItemInAndEndBeforeOrderByStartDesc(Collection<Item> items, LocalDateTime currentTime);

    Collection<Booking> findBookingsByItemInAndStartBeforeAndEndAfterOrderByStartDesc(Collection<Item> items, LocalDateTime currentTime1, LocalDateTime currentTime2);

    @Query(value = "SELECT id,\n" +
            "       start_ts,\n" +
            "       end_ts,\n" +
            "       item,\n" +
            "       booker,\n" +
            "       status\n" +
            "FROM (SELECT b.id,\n" +
            "             b.start_ts,\n" +
            "             b.end_ts,\n" +
            "             b.booker,\n" +
            "             b.item,\n" +
            "             b.status,\n" +
            "             ROW_NUMBER() OVER (PARTITION\n" +
            "                 BY\n" +
            "                 b.item\n" +
            "                 ORDER BY\n" +
            "                     b.end_ts DESC ) AS rn\n" +
            "      FROM bookings b\n" +
            "      WHERE b.item IN ?1\n" +
            "        AND (b.end_ts < CURRENT_TIMESTAMP OR (b.start_ts < CURRENT_TIMESTAMP AND b.end_ts > CURRENT_TIMESTAMP))\n" +
            "        AND b.status != 'REJECTED') s\n" +
            "WHERE s.rn = 1", nativeQuery = true)
    List<Booking> getItemsLastBookings(Collection<Item> items);

    @Query(value = "SELECT id, start_ts, end_ts, item, booker, status\n" +
            "FROM (SELECT b.id,\n" +
            "             b.start_ts,\n" +
            "             b.end_ts,\n" +
            "             b.booker,\n" +
            "             b.item,\n" +
            "             b.status,\n" +
            "             ROW_NUMBER() OVER (PARTITION BY b.item ORDER BY b.start_ts) AS rn\n" +
            "      FROM bookings b\n" +
            "      WHERE b.item IN ?1\n" +
            "        AND b.start_ts > CURRENT_TIMESTAMP\n" +
            "        AND b.status != 'REJECTED') s\n" +
            "WHERE s.rn = 1", nativeQuery = true)
    List<Booking> getItemsNextBookings(Collection<Item> items);

    Collection<Booking> getBookingsByBookerAndItemAndEndIsBeforeAndStatus(User user, Item item, LocalDateTime currentTime, BookingState state);
}
