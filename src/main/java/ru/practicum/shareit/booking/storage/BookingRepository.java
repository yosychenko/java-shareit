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
    Collection<Booking> findBookingsByItemInOrderByStartDesc(Collection<Item> items);
    Collection<Booking> findBookingsByItemInAndStatusOrderByStartDesc(Collection<Item> items, BookingStatus bookingStatus);
    Collection<Booking> findBookingsByItemInAndStartAfterOrderByStartDesc(Collection<Item> items, LocalDateTime currentTime);

    @Query(value = "SELECT * FROM bookings b WHERE b.item=?1 AND b.start_ts > current_timestamp ORDER BY b.end_ts LIMIT 1", nativeQuery = true)
    Booking getLastItemBooking(Item item);
    Booking findFirstByItemAndStartAfterOrderByStart(Item item, LocalDateTime currentTime);

}
