package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findBookingsByBookerOrderByStartDesc(User user, Pageable pageable);

    List<Booking> findBookingsByBookerAndStatusOrderByStartDesc(User user, BookingState state, Pageable pageable);

    List<Booking> findBookingsByBookerAndStartAfterOrderByStartDesc(User user, LocalDateTime currentTime, Pageable pageable);

    List<Booking> findBookingsByBookerAndEndBeforeOrderByStartDesc(User user, LocalDateTime currentTime, Pageable pageable);

    List<Booking> findBookingsByBookerAndStartBeforeAndEndAfterOrderByStartDesc(User user, LocalDateTime currentTime1, LocalDateTime currentTime2, Pageable pageable);

    List<Booking> findBookingsByItemInAndStatusNot(Collection<Item> items, BookingState state);

    List<Booking> findBookingsByItemInOrderByStartDesc(Collection<Item> items, Pageable pageable);

    List<Booking> findBookingsByItemInAndStatusOrderByStartDesc(Collection<Item> items, BookingState state, Pageable pageable);

    List<Booking> findBookingsByItemInAndStartAfterOrderByStartDesc(Collection<Item> items, LocalDateTime currentTime, Pageable pageable);

    List<Booking> findBookingsByItemInAndEndBeforeOrderByStartDesc(Collection<Item> items, LocalDateTime currentTime, Pageable pageable);

    List<Booking> findBookingsByItemInAndStartBeforeAndEndAfterOrderByStartDesc(Collection<Item> items, LocalDateTime currentTime1, LocalDateTime currentTime2, Pageable pageable);

    Collection<Booking> getBookingsByBookerAndItemAndEndIsBeforeAndStatus(User user, Item item, LocalDateTime currentTime, BookingState state);
}
