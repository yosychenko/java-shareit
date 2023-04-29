package ru.practicum.shareit.booking.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceUnitTest {

    private final LocalDateTime start = LocalDateTime.now().plusDays(1);
    private final LocalDateTime end = LocalDateTime.now().plusDays(2);
    @Mock
    private BookingRepository bookingStorage;
    @Mock
    private ItemService itemService;
    @Mock
    private UserService userService;
    @InjectMocks
    private BookingServiceImpl bookingService;
    private User owner;

    private User booker;

    private Item item;

    private Booking bookingToApprove;

    private Booking approvedBooking;

    private static Booking copyBooking(Booking originalBooking) {
        Booking copiedBooking = new Booking();
        copiedBooking.setId(originalBooking.getId());
        copiedBooking.setStart(originalBooking.getStart());
        copiedBooking.setEnd(originalBooking.getEnd());
        copiedBooking.setItem(originalBooking.getItem());
        copiedBooking.setBooker(originalBooking.getBooker());
        copiedBooking.setStatus(originalBooking.getStatus());

        return copiedBooking;
    }

    @BeforeEach
    void beforeEach() {
        owner = new User();
        owner.setId(10L);
        owner.setName("John Owner");
        owner.setEmail("john.owner@mail.com");

        booker = new User();
        booker.setId(25L);
        booker.setName("John Booker");
        booker.setEmail("john.booker@mail.com");

        item = new Item();
        item.setId(10L);
        item.setName("item_name");
        item.setAvailable(true);
        item.setOwner(owner);

        bookingToApprove = new Booking();
        bookingToApprove.setId(1L);
        bookingToApprove.setStart(start);
        bookingToApprove.setEnd(end);
        bookingToApprove.setItem(item);
        bookingToApprove.setBooker(booker);
        bookingToApprove.setStatus(BookingStatus.WAITING);

        approvedBooking = copyBooking(bookingToApprove);
        approvedBooking.setStatus(BookingStatus.APPROVED);

    }

    @Test
    void testCreateBooking() {
        Booking correctBooking = copyBooking(bookingToApprove);

        CreateBookingDto createBookingDto = CreateBookingDto.builder()
                .start(start)
                .end(end)
                .itemId(correctBooking.getItem().getId())
                .build();

        when(itemService.getItemById(anyLong())).thenReturn(item);
        when(userService.getUserById(anyLong())).thenReturn(booker);
        when(bookingStorage.save(any(Booking.class))).thenReturn(correctBooking);

        Booking createdBooking = bookingService.createBooking(booker.getId(), createBookingDto);
        assertThat(createdBooking).isNotNull();
        assertThat(createdBooking).usingRecursiveComparison().isEqualTo(correctBooking);
    }

    @Test
    void testCreateBookingItemIsNotAvailable() {
        CreateBookingDto createBookingDto = CreateBookingDto.builder()
                .start(start)
                .end(end)
                .itemId(item.getId())
                .build();

        item.setAvailable(false);

        when(itemService.getItemById(anyLong())).thenReturn(item);

        assertThatThrownBy(() -> bookingService.createBooking(booker.getId(), createBookingDto))
                .isInstanceOf(CannotBookUnavailableItemException.class)
                .hasMessageContaining("Вещь с ID=10 недоступна для бронирования.");
    }

    @Test
    void testCreateBookingItemOwnerCannotBook() {
        CreateBookingDto createBookingDto = CreateBookingDto.builder()
                .start(start)
                .end(end)
                .itemId(item.getId())
                .build();

        when(itemService.getItemById(anyLong())).thenReturn(item);

        assertThatThrownBy(() -> bookingService.createBooking(owner.getId(), createBookingDto))
                .isInstanceOf(CannotBookOwnedItemException.class)
                .hasMessageContaining("Пользователь с ID=10 не может забронировать свою вещь с ID=10.");
    }

    @Test
    void testApproveBooking() {
        when(userService.getUserById(anyLong())).thenReturn(owner);
        when(bookingStorage.findById(anyLong())).thenReturn(Optional.of(bookingToApprove));
        when(bookingStorage.save(any(Booking.class))).thenReturn(approvedBooking);

        Booking resultBooking = bookingService.approveBooking(owner.getId(), bookingToApprove.getId(), true);

        assertThat(resultBooking).isNotNull();
        assertThat(resultBooking).usingRecursiveComparison().isEqualTo(approvedBooking);
    }

    @Test
    void testRejectBooking() {
        Booking rejectedBooking = copyBooking(bookingToApprove);
        rejectedBooking.setStatus(BookingStatus.REJECTED);

        when(userService.getUserById(anyLong())).thenReturn(owner);
        when(bookingStorage.findById(anyLong())).thenReturn(Optional.of(bookingToApprove));
        when(bookingStorage.save(any(Booking.class))).thenReturn(rejectedBooking);

        Booking resultBooking = bookingService.approveBooking(owner.getId(), bookingToApprove.getId(), false);

        assertThat(resultBooking).isNotNull();
        assertThat(resultBooking).usingRecursiveComparison().isEqualTo(rejectedBooking);
    }

    @Test
    void testCannotApproveBookingIfUserIsNotAnOwner() {
        when(userService.getUserById(anyLong())).thenReturn(booker);
        when(bookingStorage.findById(anyLong())).thenReturn(Optional.of(bookingToApprove));

        assertThatThrownBy(() -> bookingService.approveBooking(booker.getId(), bookingToApprove.getId(), false))
                .isInstanceOf(CannotApproveBookingException.class)
                .hasMessageContaining("Нельзя изменить статус бронирования для вещи с ID=10 - пользователь с ID=25 не является ее владельцем.");
    }

    @Test
    void testCannotApproveBookingUsingSameStatus() {
        when(userService.getUserById(anyLong())).thenReturn(owner);
        when(bookingStorage.findById(anyLong())).thenReturn(Optional.of(approvedBooking));

        assertThatThrownBy(() -> bookingService.approveBooking(owner.getId(), approvedBooking.getId(), true))
                .isInstanceOf(SameApproveStatusException.class)
                .hasMessageContaining("Бронирование c ID=1 уже имеет статус APPROVED, выберите другой статус.");
    }

    @Test
    void testGetBookingById() {
        Booking bookingToGet = copyBooking(bookingToApprove);

        when(bookingStorage.findById(anyLong())).thenReturn(Optional.of(bookingToGet));

        Booking booking = bookingService.getBookingById(owner.getId(), bookingToGet.getId());

        assertThat(booking).isNotNull();
        assertThat(booking).usingRecursiveComparison().isEqualTo(bookingToGet);
    }

    @Test
    void testGetBookingByIdCannotGetIfUserIsNotAnOwner() {
        Booking bookingToGet = copyBooking(bookingToApprove);

        when(bookingStorage.findById(anyLong())).thenReturn(Optional.of(bookingToGet));

        assertThatThrownBy(() -> bookingService.getBookingById(100L, bookingToGet.getId()))
                .isInstanceOf(UserHasNoAccessToBookingException.class)
                .hasMessageContaining("Пользователь с ID=100 не является автором брони с ID=1 или владельцем забронированной вещи.");
    }

    @Test
    void testGetUserBookings() {
        when(userService.getUserById(anyLong())).thenReturn(booker);

        BookingState bookingState = BookingState.ALL;
        when(bookingStorage.findBookingsByBookerOrderByStartDesc(any(User.class), any(Pageable.class))).thenReturn(List.of(bookingToApprove));
        Collection<Booking> bookingsALL = bookingService.getUserBookings(booker.getId(), bookingState, PageRequest.of(0, 2000));
        assertThat(bookingsALL).isNotEmpty();
        assertThat(bookingsALL).contains(bookingToApprove);

        bookingState = BookingState.FUTURE;
        Booking futureBooking = copyBooking(bookingToApprove);
        futureBooking.setStart(LocalDateTime.now().plusDays(10));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));
        when(bookingStorage.findBookingsByBookerAndStartAfterOrderByStartDesc(any(User.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(futureBooking));
        Collection<Booking> bookingsFUTURE = bookingService.getUserBookings(booker.getId(), bookingState, PageRequest.of(0, 2000));
        assertThat(bookingsFUTURE).isNotEmpty();
        assertThat(bookingsFUTURE).contains(futureBooking);

        bookingState = BookingState.PAST;
        Booking pastBooking = copyBooking(bookingToApprove);
        pastBooking.setStart(LocalDateTime.now().minusDays(10));
        pastBooking.setEnd(LocalDateTime.now().minusDays(8));
        when(bookingStorage.findBookingsByBookerAndEndBeforeOrderByStartDesc(any(User.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(pastBooking));
        Collection<Booking> bookingsPAST = bookingService.getUserBookings(booker.getId(), bookingState, PageRequest.of(0, 2000));
        assertThat(bookingsPAST).isNotEmpty();
        assertThat(bookingsPAST).contains(pastBooking);

        bookingState = BookingState.CURRENT;
        Booking currentBooking = copyBooking(bookingToApprove);
        currentBooking.setStart(LocalDateTime.now().minusDays(1));
        currentBooking.setEnd(LocalDateTime.now().plusDays(2));
        when(bookingStorage.findBookingsByBookerAndStartBeforeAndEndAfterOrderByStartDesc(any(User.class), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(currentBooking));
        Collection<Booking> bookingsCURRENT = bookingService.getUserBookings(booker.getId(), bookingState, PageRequest.of(0, 2000));
        assertThat(bookingsCURRENT).isNotEmpty();
        assertThat(bookingsCURRENT).contains(currentBooking);

        bookingState = BookingState.REJECTED;
        Booking rejectedBooking = copyBooking(bookingToApprove);
        rejectedBooking.setStart(LocalDateTime.now().minusDays(1));
        rejectedBooking.setEnd(LocalDateTime.now().plusDays(2));
        rejectedBooking.setStatus(BookingStatus.REJECTED);
        when(bookingStorage.findBookingsByBookerAndStatusOrderByStartDesc(any(User.class), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(List.of(rejectedBooking));
        Collection<Booking> bookingsREJECTED = bookingService.getUserBookings(booker.getId(), bookingState, PageRequest.of(0, 2000));
        assertThat(bookingsREJECTED).isNotEmpty();
        assertThat(bookingsREJECTED).contains(rejectedBooking);
    }

    @Test
    void testGetOwnedItemsBookings() {
        when(itemService.getUserItems(anyLong())).thenReturn(List.of(item));

        BookingState bookingState = BookingState.ALL;
        when(bookingStorage.findBookingsByItemInOrderByStartDesc(anyCollection(), any(Pageable.class))).thenReturn(List.of(bookingToApprove));
        Collection<Booking> bookingsALL = bookingService.getOwnedItemsBookings(owner.getId(), bookingState, PageRequest.of(0, 2000));
        assertThat(bookingsALL).isNotEmpty();
        assertThat(bookingsALL).contains(bookingToApprove);

        bookingState = BookingState.FUTURE;
        Booking futureBooking = copyBooking(bookingToApprove);
        futureBooking.setStart(LocalDateTime.now().plusDays(10));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));
        when(bookingStorage.findBookingsByItemInAndStartAfterOrderByStartDesc(anyCollection(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(futureBooking));
        Collection<Booking> bookingsFUTURE = bookingService.getOwnedItemsBookings(owner.getId(), bookingState, PageRequest.of(0, 2000));
        assertThat(bookingsFUTURE).isNotEmpty();
        assertThat(bookingsFUTURE).contains(futureBooking);

        bookingState = BookingState.PAST;
        Booking pastBooking = copyBooking(bookingToApprove);
        pastBooking.setStart(LocalDateTime.now().minusDays(10));
        pastBooking.setEnd(LocalDateTime.now().minusDays(8));
        when(bookingStorage.findBookingsByItemInAndEndBeforeOrderByStartDesc(anyCollection(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(pastBooking));
        Collection<Booking> bookingsPAST = bookingService.getOwnedItemsBookings(owner.getId(), bookingState, PageRequest.of(0, 2000));
        assertThat(bookingsPAST).isNotEmpty();
        assertThat(bookingsPAST).contains(pastBooking);

        bookingState = BookingState.CURRENT;
        Booking currentBooking = copyBooking(bookingToApprove);
        currentBooking.setStart(LocalDateTime.now().minusDays(1));
        currentBooking.setEnd(LocalDateTime.now().plusDays(2));
        when(bookingStorage.findBookingsByItemInAndStartBeforeAndEndAfterOrderByStartDesc(anyCollection(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(currentBooking));
        Collection<Booking> bookingsCURRENT = bookingService.getOwnedItemsBookings(owner.getId(), bookingState, PageRequest.of(0, 2000));
        assertThat(bookingsCURRENT).isNotEmpty();
        assertThat(bookingsCURRENT).contains(currentBooking);

        bookingState = BookingState.REJECTED;
        Booking rejectedBooking = copyBooking(bookingToApprove);
        rejectedBooking.setStart(LocalDateTime.now().minusDays(1));
        rejectedBooking.setEnd(LocalDateTime.now().plusDays(2));
        rejectedBooking.setStatus(BookingStatus.REJECTED);
        when(bookingStorage.findBookingsByItemInAndStatusOrderByStartDesc(anyCollection(), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(List.of(rejectedBooking));
        Collection<Booking> bookingsREJECTED = bookingService.getOwnedItemsBookings(owner.getId(), bookingState, PageRequest.of(0, 2000));
        assertThat(bookingsREJECTED).isNotEmpty();
        assertThat(bookingsREJECTED).contains(rejectedBooking);
    }

}
