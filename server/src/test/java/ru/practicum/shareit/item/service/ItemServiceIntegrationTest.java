package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemServiceIntegrationTest {
    private final EntityManager em;

    private final ItemService itemService;

    private Item item;

    private Item itemWithoutBookingsAndComments;

    private Booking lastBooking;

    private Booking nextBooking;

    private Comment comment;

    @BeforeEach
    void beforeEach() {
        User owner = new User();
        owner.setName("John");
        owner.setEmail("john.doe@mail.com");

        User booker = new User();
        booker.setName("Booker");
        booker.setEmail("john.booker@mail.com");

        item = new Item();
        item.setName("item_name");
        item.setDescription("item_description");
        item.setAvailable(true);
        item.setOwner(owner);

        itemWithoutBookingsAndComments = new Item();
        itemWithoutBookingsAndComments.setName("itemWithoutBookings");
        itemWithoutBookingsAndComments.setDescription("item_description");
        itemWithoutBookingsAndComments.setAvailable(true);
        itemWithoutBookingsAndComments.setOwner(booker);

        lastBooking = new Booking();
        lastBooking.setStart(LocalDateTime.now().minusDays(7));
        lastBooking.setEnd(LocalDateTime.now().minusDays(2));
        lastBooking.setItem(item);
        lastBooking.setBooker(booker);
        lastBooking.setStatus(BookingState.WAITING);

        nextBooking = new Booking();
        nextBooking.setStart(LocalDateTime.now().plusDays(4));
        nextBooking.setEnd(LocalDateTime.now().plusDays(5));
        nextBooking.setItem(item);
        nextBooking.setBooker(booker);
        nextBooking.setStatus(BookingState.WAITING);

        comment = new Comment();
        comment.setText("comment");
        comment.setAuthor(booker);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.of(2023, 4, 20, 10, 0));

        em.persist(owner);
        em.persist(booker);
        em.persist(item);
        em.persist(itemWithoutBookingsAndComments);
        em.persist(lastBooking);
        em.persist(nextBooking);
        em.persist(comment);
        em.flush();
    }

    @Test
    void testGetUserItemsWithBookingIntervals() {
        ArrayList<ItemDto> result = new ArrayList<>(itemService.getUserItemsWithBookingIntervals(1L, PageRequest.of(0, 2000)));

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);

        ItemDto itemDto = result.get(0);
        assertThat(itemDto.getId()).isEqualTo(1L);
        assertThat(itemDto.getName()).isEqualTo(item.getName());
        assertThat(itemDto.getDescription()).isEqualTo(item.getDescription());
        assertThat(itemDto.getAvailable()).isEqualTo(item.getAvailable());
        assertThat(itemDto.getLastBooking()).isEqualTo(BookingMapper.toBookingTimeIntervalDto(lastBooking));
        assertThat(itemDto.getNextBooking()).isEqualTo(BookingMapper.toBookingTimeIntervalDto(nextBooking));
        assertThat(itemDto.getComments()).hasSize(1);
        assertThat(new ArrayList<>(itemDto.getComments())).contains(CommentMapper.toCommentDto(comment));
        assertThat(itemDto.getRequestId()).isNull();

    }

    @Test
    void testGetUserItemsWithBookingIntervalsNoBookingIntervals() {
        ArrayList<ItemDto> result = new ArrayList<>(itemService.getUserItemsWithBookingIntervals(2L, PageRequest.of(0, 2000)));

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);

        ItemDto itemDto = result.get(0);
        assertThat(itemDto.getId()).isEqualTo(2L);
        assertThat(itemDto.getName()).isEqualTo(itemWithoutBookingsAndComments.getName());
        assertThat(itemDto.getDescription()).isEqualTo(itemWithoutBookingsAndComments.getDescription());
        assertThat(itemDto.getAvailable()).isEqualTo(itemWithoutBookingsAndComments.getAvailable());
        assertThat(itemDto.getLastBooking()).isNull();
        assertThat(itemDto.getNextBooking()).isNull();
        assertThat(itemDto.getComments()).isNull();
        assertThat(itemDto.getRequestId()).isNull();

    }
}
