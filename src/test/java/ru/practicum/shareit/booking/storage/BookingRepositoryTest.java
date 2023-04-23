package ru.practicum.shareit.booking.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext
public class BookingRepositoryTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private BookingRepository bookingRepository;

    private Booking booking1;

    private Booking booking2Rejected;

    private Booking booking3;

    private Booking booking4;

    private Booking booking5;

    private Booking booking6;

    private Item item;

    private Item itemHasOnlyNextBooking;

    private Item itemHasOnlyLastBooking;

    @BeforeEach
    public void beforeEach() {
        User owner = new User();
        owner.setName("John");
        owner.setEmail("john.doe@mail.com");

        User booker = new User();
        booker.setName("Booker");
        booker.setEmail("john.booker@mail.com");

        item = new Item();
        item.setName("Отвертка 1");
        item.setDescription("С Аккумулятором");
        item.setAvailable(true);
        item.setOwner(owner);

        itemHasOnlyNextBooking = new Item();
        itemHasOnlyNextBooking.setName("Отвертка 2");
        itemHasOnlyNextBooking.setDescription("С Аккумулятором");
        itemHasOnlyNextBooking.setAvailable(true);
        itemHasOnlyNextBooking.setOwner(owner);

        itemHasOnlyLastBooking = new Item();
        itemHasOnlyLastBooking.setName("Отвертка 3");
        itemHasOnlyLastBooking.setDescription("С Аккумулятором");
        itemHasOnlyLastBooking.setAvailable(true);
        itemHasOnlyLastBooking.setOwner(owner);

        booking1 = new Booking();
        booking1.setStart(LocalDateTime.now().minusDays(7));
        booking1.setEnd(LocalDateTime.now().minusDays(2));
        booking1.setItem(item);
        booking1.setBooker(booker);
        booking1.setStatus(BookingState.WAITING);

        booking2Rejected = new Booking();
        booking2Rejected.setStart(LocalDateTime.now().plusDays(1));
        booking2Rejected.setEnd(LocalDateTime.now().plusDays(2));
        booking2Rejected.setItem(item);
        booking2Rejected.setBooker(booker);
        booking2Rejected.setStatus(BookingState.REJECTED);

        booking3 = new Booking();
        booking3.setStart(LocalDateTime.now().plusDays(4));
        booking3.setEnd(LocalDateTime.now().plusDays(5));
        booking3.setItem(item);
        booking3.setBooker(booker);
        booking3.setStatus(BookingState.WAITING);

        booking4 = new Booking();
        booking4.setStart(LocalDateTime.now().plusDays(10));
        booking4.setEnd(LocalDateTime.now().plusDays(15));
        booking4.setItem(itemHasOnlyNextBooking);
        booking4.setBooker(booker);
        booking4.setStatus(BookingState.WAITING);

        booking5 = new Booking();
        booking5.setStart(LocalDateTime.now().minusDays(15));
        booking5.setEnd(LocalDateTime.now().minusDays(10));
        booking5.setItem(itemHasOnlyLastBooking);
        booking5.setBooker(booker);
        booking5.setStatus(BookingState.WAITING);

        booking6 = new Booking();
        booking6.setStart(LocalDateTime.now().minusDays(20));
        booking6.setEnd(LocalDateTime.now().minusDays(15));
        booking6.setItem(itemHasOnlyLastBooking);
        booking6.setBooker(booker);
        booking6.setStatus(BookingState.WAITING);


        em.persist(owner);
        em.persist(booker);
        em.persist(item);
        em.persist(itemHasOnlyNextBooking);
        em.persist(itemHasOnlyLastBooking);
        em.persist(booking1);
        em.persist(booking2Rejected);
        em.persist(booking3);
        em.persist(booking4);
        em.persist(booking5);
        em.persist(booking6);
        em.flush();
    }

    @Test
    void testGetItemLastAndNextBookings() {
        List<Booking> lastBookings = bookingRepository.getItemsLastBookings(List.of(item));
        List<Booking> nextBookings = bookingRepository.getItemsNextBookings(List.of(item));

        assertThat(lastBookings).isNotEmpty();
        assertThat(lastBookings).hasSize(1);
        assertThat(lastBookings).contains(booking1);

        assertThat(nextBookings).isNotEmpty();
        assertThat(nextBookings).hasSize(1);
        assertThat(nextBookings).contains(booking3);
        assertThat(nextBookings).doesNotContain(booking2Rejected);
    }

    @Test
    void testGetItemsOnlyNextBookings() {
        List<Booking> lastBookings = bookingRepository.getItemsLastBookings(List.of(itemHasOnlyNextBooking));
        List<Booking> nextBookings = bookingRepository.getItemsNextBookings(List.of(itemHasOnlyNextBooking));

        assertThat(lastBookings).isEmpty();

        assertThat(nextBookings).isNotEmpty();
        assertThat(nextBookings).hasSize(1);
        assertThat(nextBookings).contains(booking4);
    }

    @Test
    void testGetItemsOnlyLastBookingsPickNearest() {
        List<Booking> lastBookings = bookingRepository.getItemsLastBookings(List.of(itemHasOnlyLastBooking));
        List<Booking> nextBookings = bookingRepository.getItemsNextBookings(List.of(itemHasOnlyLastBooking));


        assertThat(lastBookings).isNotEmpty();
        assertThat(lastBookings).hasSize(1);
        assertThat(lastBookings).contains(booking5);
        assertThat(lastBookings).doesNotContain(booking6);

        assertThat(nextBookings).isEmpty();

    }
}
