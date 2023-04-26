package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingServiceIntegrationTest {
    private final EntityManager em;

    private final BookingService bookingService;
    private final LocalDateTime start = LocalDateTime.now().plusDays(1);
    private final LocalDateTime end = LocalDateTime.now().plusDays(2);
    private Item item;
    private User booker;

    @BeforeEach
    void beforeEach() {

        User owner = new User();
        owner.setName("John Owner");
        owner.setEmail("john.owner@mail.com");

        booker = new User();
        booker.setName("John Booker");
        booker.setEmail("john.booker@mail.com");

        item = new Item();
        item.setName("Отвертка 1");
        item.setDescription("С Аккумулятором");
        item.setAvailable(true);
        item.setOwner(owner);

        em.persist(owner);
        em.persist(booker);
        em.persist(item);
        em.flush();
    }

    @Test
    void testCreateBooking() {
        CreateBookingDto createBookingDto = CreateBookingDto.builder()
                .start(start)
                .end(end)
                .itemId(1L)
                .build();

        bookingService.createBooking(2L, createBookingDto);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking createdBooking = query.setParameter("id", 1L).getSingleResult();

        assertThat(createdBooking).isNotNull();
        assertThat(createdBooking.getId()).isEqualTo(1L);
        assertThat(createdBooking.getStart()).isEqualTo(start);
        assertThat(createdBooking.getEnd()).isEqualTo(end);
        assertThat(createdBooking.getItem()).usingRecursiveComparison().isEqualTo(item);
        assertThat(createdBooking.getBooker()).usingRecursiveComparison().isEqualTo(booker);
        assertThat(createdBooking.getStatus()).isEqualTo(BookingState.WAITING);
    }
}
