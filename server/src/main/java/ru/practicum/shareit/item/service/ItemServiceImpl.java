package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.CannotLeaveCommentException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
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
import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemStorage;
    private final CommentRepository commentStorage;
    private final BookingRepository bookingStorage;
    private final UserService userService;
    private final ItemRequestService itemRequestService;


    @Autowired
    public ItemServiceImpl(
            ItemRepository itemStorage,
            CommentRepository commentStorage,
            BookingRepository bookingStorage,
            UserService userService,
            ItemRequestService itemRequestService
    ) {
        this.itemStorage = itemStorage;
        this.commentStorage = commentStorage;
        this.bookingStorage = bookingStorage;
        this.userService = userService;
        this.itemRequestService = itemRequestService;
    }

    @Override
    @Transactional
    public Item createItem(long userId, ItemDto newItemDto) {
        User owner = userService.getUserById(userId);

        Item item = new Item();
        if (newItemDto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestService.getItemRequestById(owner.getId(), newItemDto.getRequestId());
            item.setRequest(itemRequest);
        }
        item.setName(newItemDto.getName());
        item.setDescription(newItemDto.getDescription());
        item.setAvailable(newItemDto.getAvailable());
        item.setOwner(owner);


        return itemStorage.save(item);
    }

    @Override
    @Transactional
    public Comment addComment(long userId, long itemId, Comment newComment) {
        User user = userService.getUserById(userId);
        Item item = getItemById(itemId);
        Collection<Booking> finishedBookings = bookingStorage.getBookingsByBookerAndItemAndEndIsBeforeAndStatus(
                user, item, LocalDateTime.now(), BookingStatus.APPROVED
        );

        if (finishedBookings.isEmpty()) {
            throw new CannotLeaveCommentException(userId, itemId);
        }

        newComment.setItem(item);
        newComment.setAuthor(user);
        newComment.setCreated(LocalDateTime.now());

        return commentStorage.save(newComment);
    }

    private Collection<Comment> getCommentsByItems(Collection<Item> items) {
        return commentStorage.getCommentsByItemIn(items);
    }

    @Override
    @Transactional
    public Item updateItem(long userId, long itemId, Item newItem) {
        Item itemToUpdate = getItemById(itemId);
        User user = userService.getUserById(userId);

        if (itemToUpdate.getOwner().getId() != user.getId()) {
            throw new UserIsNotOwnerException(userId, itemId);
        }
        Item patchedItem = new Item();

        patchedItem.setId(itemToUpdate.getId());
        patchedItem.setName(itemToUpdate.getName());
        patchedItem.setDescription(itemToUpdate.getDescription());
        patchedItem.setAvailable(itemToUpdate.getAvailable());
        patchedItem.setOwner(itemToUpdate.getOwner());
        patchedItem.setRequest(itemToUpdate.getRequest());

        if (newItem.getName() != null) {
            patchedItem.setName(newItem.getName());
        }
        if (newItem.getDescription() != null) {
            patchedItem.setDescription(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            patchedItem.setAvailable(newItem.getAvailable());
        }

        return itemStorage.save(patchedItem);
    }

    @Override
    public Item getItemById(long itemId) {
        return itemStorage.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));
    }

    @Override
    public ItemDto getItemByIdWithBookingIntervals(long userId, long itemId) {
        User user = userService.getUserById(userId);
        Item item = getItemById(itemId);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        Collection<Comment> comments = getCommentsByItems(List.of(item));
        itemDto.setComments(comments.stream().map(CommentMapper::toCommentDto).collect(toList()));

        Collection<Booking> itemBookings = bookingStorage.findBookingsByItemInAndStatusNot(List.of(item), BookingStatus.REJECTED);

        if (item.getOwner().getId() == user.getId() && !itemBookings.isEmpty()) {
            LocalDateTime currentTime = LocalDateTime.now();
            getLastAndNextBookings(itemDto, itemBookings, currentTime);
        }

        return itemDto;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Item> getUserItems(long userId) {
        User user = userService.getUserById(userId);
        return itemStorage.findItemsByOwner(user);
    }

    private Collection<Item> getUserItemsPageable(long userId, Pageable pageable) {
        User user = userService.getUserById(userId);
        return itemStorage.findItemsByOwnerId(user.getId(), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemDto> getUserItemsWithBookingIntervals(long userId, Pageable pageable) {
        List<ItemDto> itemDtos = new ArrayList<>();
        List<ItemDto> itemDtosNullIntervals = new ArrayList<>();

        User user = userService.getUserById(userId);
        Collection<Item> items = getUserItemsPageable(user.getId(), pageable);
        Collection<Booking> bookings = bookingStorage.findBookingsByItemInAndStatusNot(items, BookingStatus.REJECTED);
        Collection<Comment> comments = commentStorage.getCommentsByItemIn(items);

        // Загрузим комментарии в Map
        Map<Item, List<Comment>> itemToComments = comments
                .stream()
                .collect(groupingBy(Comment::getItem, toList()));

        // Загрузим бронирования в Map
        Map<Item, List<Booking>> itemToBookings = bookings
                .stream()
                .collect(groupingBy(Booking::getItem, toList()));

        LocalDateTime currentTime = LocalDateTime.now();
        for (Item item : items) {
            ItemDto itemDto = ItemMapper.toItemDto(item);

            List<Comment> itemComments = itemToComments.get(item);
            if (itemComments != null) {
                itemDto.setComments(itemComments.stream().map(CommentMapper::toCommentDto).collect(toList()));
            }

            List<Booking> itemBookings = itemToBookings.get(item);
            if (itemBookings != null) {
                getLastAndNextBookings(itemDto, itemBookings, currentTime);
            } else {
                itemDtosNullIntervals.add(itemDto);
                continue;
            }

            itemDtos.add(itemDto);
        }

        // Вещи без известных интервалов бронирования - в конце списка
        itemDtos.addAll(itemDtosNullIntervals);

        return itemDtos;
    }

    @Override
    public Collection<Item> searchItems(String text, Pageable pageable) {
        if (text.isBlank()) {
            return List.of();
        }

        return itemStorage.searchItems(text.toLowerCase(), pageable);
    }

    private Optional<Booking> findLastBooking(Collection<Booking> bookings, LocalDateTime currentTime) {
        return bookings
                .stream()
                .filter(booking ->
                        booking.getEnd().isBefore(currentTime) ||
                                (booking.getStart().isBefore(currentTime) && booking.getEnd().isAfter(currentTime))
                ).max(Comparator.comparing(Booking::getEnd));

    }

    private Optional<Booking> findNextBooking(Collection<Booking> bookings, LocalDateTime currentTime) {
        return bookings
                .stream()
                .filter(booking -> booking.getStart().isAfter(currentTime))
                .min(Comparator.comparing(Booking::getStart));
    }

    private void getLastAndNextBookings(ItemDto itemDto, Collection<Booking> itemBookings, LocalDateTime currentTime) {
        Optional<Booking> lastBooking = findLastBooking(itemBookings, currentTime);
        Optional<Booking> nextBooking = findNextBooking(itemBookings, currentTime);

        lastBooking.ifPresent(booking -> itemDto.setLastBooking(BookingMapper.toBookingTimeIntervalDto(booking)));
        nextBooking.ifPresent(booking -> itemDto.setNextBooking(BookingMapper.toBookingTimeIntervalDto(booking)));
    }
}
