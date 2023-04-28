package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.CannotLeaveCommentException;
import ru.practicum.shareit.item.exception.UserIsNotOwnerException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
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
public class ItemServiceUnitTest {
    @Mock
    private ItemRepository itemStorage;

    @Mock
    private CommentRepository commentStorage;

    @Mock
    private BookingRepository bookingStorage;

    @Mock
    private UserService userService;

    @Mock
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User owner;

    private User requestor;

    private Item item;

    private Comment comment;

    private static Item copyItem(Item originalItem) {
        Item copiedItem = new Item();
        copiedItem.setId(originalItem.getId());
        copiedItem.setName(originalItem.getName());
        copiedItem.setDescription(originalItem.getDescription());
        copiedItem.setAvailable(originalItem.getAvailable());
        copiedItem.setOwner(originalItem.getOwner());
        copiedItem.setRequest(originalItem.getRequest());

        return copiedItem;
    }

    @BeforeEach
    void beforeEach() {
        owner = new User();
        owner.setId(10L);
        owner.setName("John Owner");
        owner.setEmail("john.owner@mail.com");

        item = new Item();
        item.setId(10L);
        item.setName("item_name");
        item.setAvailable(true);
        item.setOwner(owner);

        requestor = new User();
        requestor.setId(25L);
        requestor.setName("Booker");
        requestor.setEmail("john.booker@mail.com");

        comment = new Comment();
        comment.setText("comment");
        comment.setAuthor(owner);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.of(2023, 4, 20, 10, 0));
    }

    @Test
    void testCreateItem() {
        ItemDto itemDto = ItemMapper.toItemDto(item);

        when(userService.getUserById(anyLong())).thenReturn(owner);
        when(itemStorage.save(any(Item.class))).thenReturn(item);

        Item createdItem = itemService.createItem(owner.getId(), itemDto);

        assertThat(createdItem).isNotNull();
        assertThat(createdItem).usingRecursiveComparison().isEqualTo(item);

    }

    @Test
    void testCreateItemWithRequest() {
        User requestor = new User();
        requestor.setId(25L);
        requestor.setName("Booker");
        requestor.setEmail("john.booker@mail.com");

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Нужна отвертка с аккумулятором");
        itemRequest.setRequestor(requestor);

        Item itemWithRequest = copyItem(item);
        itemWithRequest.setRequest(itemRequest);

        ItemDto itemDto = ItemMapper.toItemDto(itemWithRequest);

        when(userService.getUserById(anyLong())).thenReturn(owner);
        when(itemRequestService.getItemRequestById(anyLong(), anyLong())).thenReturn(itemRequest);
        when(itemStorage.save(any(Item.class))).thenReturn(itemWithRequest);

        Item createdItem = itemService.createItem(owner.getId(), itemDto);

        assertThat(createdItem).isNotNull();
        assertThat(createdItem).usingRecursiveComparison().isEqualTo(itemWithRequest);
    }

    @Test
    void testAddComment() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(new User());
        booking.setStatus(BookingStatus.WAITING);

        when(userService.getUserById(anyLong())).thenReturn(requestor);
        when(itemStorage.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(
                bookingStorage.getBookingsByBookerAndItemAndEndIsBeforeAndStatus(any(User.class), any(Item.class), any(LocalDateTime.class), any(BookingStatus.class))
        ).thenReturn(List.of(booking));

        when(commentStorage.save(any(Comment.class))).thenReturn(comment);

        Comment addedComment = itemService.addComment(owner.getId(), item.getId(), comment);

        assertThat(addedComment).isNotNull();
        assertThat(addedComment).usingRecursiveComparison().isEqualTo(comment);
    }

    @Test
    void testAddCommentWithoutFinishedBookings() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(new User());
        booking.setStatus(BookingStatus.WAITING);

        Comment comment = new Comment();
        comment.setText("comment");
        comment.setAuthor(owner);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.of(2023, 4, 20, 10, 0));


        when(userService.getUserById(anyLong())).thenReturn(requestor);
        when(itemStorage.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(
                bookingStorage.getBookingsByBookerAndItemAndEndIsBeforeAndStatus(any(User.class), any(Item.class), any(LocalDateTime.class), any(BookingStatus.class))
        ).thenReturn(List.of());

        assertThatThrownBy(() -> itemService.addComment(owner.getId(), item.getId(), comment))
                .isInstanceOf(CannotLeaveCommentException.class)
                .hasMessageContaining("Пользователь с ID=10 не может оставить комментарий к вещи с ID=10");

    }

    @Test
    void testUpdateItem() {
        Item newItem = copyItem(item);
        newItem.setDescription("updated_description");

        when(itemStorage.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(userService.getUserById(anyLong())).thenReturn(owner);
        when(itemStorage.save(any(Item.class))).thenReturn(newItem);

        Item updatedItem = itemService.updateItem(owner.getId(), item.getId(), newItem);

        assertThat(updatedItem).isNotNull();
        assertThat(updatedItem).isEqualTo(newItem);
    }

    @Test
    void testUpdateItemThatUserDoesntOwn() {
        Item newItem = copyItem(item);
        newItem.setDescription("updated_description");

        when(itemStorage.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(userService.getUserById(anyLong())).thenReturn(requestor);

        assertThatThrownBy(() -> itemService.updateItem(requestor.getId(), item.getId(), newItem))
                .isInstanceOf(UserIsNotOwnerException.class)
                .hasMessageContaining("Нельзя обновить вещь. Пользователь c ID=25 не является владельцем вещи с ID=10.");
    }

    @Test
    void testGetItemByIdWithBookingIntervals() {
        Booking lastBooking = new Booking();
        lastBooking.setStart(LocalDateTime.now().minusDays(7));
        lastBooking.setEnd(LocalDateTime.now().minusDays(2));
        lastBooking.setItem(item);
        lastBooking.setBooker(requestor);
        lastBooking.setStatus(BookingStatus.WAITING);

        Booking nextBooking = new Booking();
        nextBooking.setStart(LocalDateTime.now().plusDays(2));
        nextBooking.setEnd(LocalDateTime.now().plusDays(7));
        nextBooking.setItem(item);
        nextBooking.setBooker(requestor);
        nextBooking.setStatus(BookingStatus.WAITING);

        ItemDto expectedItemDto = ItemMapper.toItemDto(item);
        expectedItemDto.setComments(List.of(CommentMapper.toCommentDto(comment)));

        when(itemStorage.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(userService.getUserById(anyLong())).thenReturn(requestor);
        when(commentStorage.getCommentsByItemIn(anyCollection())).thenReturn(List.of(comment));
        when(bookingStorage.findBookingsByItemInAndStatusNot(anyCollection(), any(BookingStatus.class))).thenReturn(List.of(lastBooking, nextBooking));

        ItemDto resultItemDto = itemService.getItemByIdWithBookingIntervals(requestor.getId(), item.getId());

        assertThat(resultItemDto).isNotNull();
        assertThat(resultItemDto).isEqualTo(expectedItemDto);
    }

    @Test
    void testGetItemByIdWithBookingIntervalsAsOwnerOfTheItem() {
        Booking lastBooking = new Booking();
        lastBooking.setStart(LocalDateTime.now().minusDays(7));
        lastBooking.setEnd(LocalDateTime.now().minusDays(2));
        lastBooking.setItem(item);
        lastBooking.setBooker(requestor);
        lastBooking.setStatus(BookingStatus.WAITING);

        Booking nextBooking = new Booking();
        nextBooking.setStart(LocalDateTime.now().plusDays(4));
        nextBooking.setEnd(LocalDateTime.now().plusDays(5));
        nextBooking.setItem(item);
        nextBooking.setBooker(requestor);
        nextBooking.setStatus(BookingStatus.WAITING);

        ItemDto expectedItemDto = ItemMapper.toItemDto(item);
        expectedItemDto.setComments(List.of(CommentMapper.toCommentDto(comment)));
        expectedItemDto.setLastBooking(BookingMapper.toBookingTimeIntervalDto(lastBooking));
        expectedItemDto.setNextBooking(BookingMapper.toBookingTimeIntervalDto(nextBooking));

        when(itemStorage.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(userService.getUserById(anyLong())).thenReturn(owner);
        when(commentStorage.getCommentsByItemIn(anyCollection())).thenReturn(List.of(comment));
        when(bookingStorage.findBookingsByItemInAndStatusNot(anyCollection(), any(BookingStatus.class))).thenReturn(List.of(nextBooking, lastBooking));

        ItemDto resultItemDto = itemService.getItemByIdWithBookingIntervals(owner.getId(), item.getId());

        assertThat(resultItemDto).isNotNull();
        assertThat(resultItemDto).isEqualTo(expectedItemDto);
    }

    @Test
    void testSearchItems() {
        when(itemStorage.searchItems(anyString(), any(Pageable.class))).thenReturn(List.of(item));

        Collection<Item> foundItems = itemService.searchItems("item_name", PageRequest.of(0, 2000));

        assertThat(foundItems).isNotEmpty();
        assertThat(foundItems).contains(item);
    }

    @Test
    void testSearchItemsEmptyString() {
        Collection<Item> foundItems = itemService.searchItems("", PageRequest.of(0, 2000));

        assertThat(foundItems).isEmpty();
    }
}
