package ru.practicum.shareit.booking.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingRepositoryTest {
    @Autowired
    private EntityManager em;

    @Autowired
    private BookingRepository bookingRepository;

    private User owner;

    private User booker;

    private Item item;

    @BeforeEach
    void beforeEach() {
        owner = new User();
        owner.setName("John");
        owner.setEmail("john.doe@mail.com");

        booker = new User();
        booker.setName("Booker");
        booker.setEmail("john.booker@mail.com");

        item = new Item();
        item.setName("Отвертка c Запросом");
        item.setDescription("С Аккумулятором и Запросом");
        item.setAvailable(true);
        item.setOwner(owner);

        em.persist(owner);
        em.persist(booker);
        em.persist(item);
        em.flush();
    }

    @Test
    void testCreateBooking() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingState.WAITING);

        bookingRepository.save(booking);

        long bookingId = 1L;

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking bookingFromDb = query.setParameter("id", bookingId).getSingleResult();

        assertThat(bookingFromDb).isNotNull();
        assertThat(bookingFromDb.getId()).isEqualTo(bookingId);
        assertThat(bookingFromDb.getStart()).isEqualTo(booking.getStart());
        assertThat(bookingFromDb.getEnd()).isEqualTo(booking.getEnd());
        assertThat(bookingFromDb.getItem()).isEqualTo(booking.getItem());
        assertThat(bookingFromDb.getBooker()).isEqualTo(booking.getBooker());
        assertThat(bookingFromDb.getStatus()).isEqualTo(booking.getStatus());
    }

    @Test
    void testCreateBookingEmptyStartDate() {
        Booking booking = new Booking();
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingState.WAITING);

        assertThatThrownBy(() -> bookingRepository.save(booking)).isInstanceOf(DataIntegrityViolationException.class);
    }
}
