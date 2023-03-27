package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.exception.CannotLeaveCommentException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.UserIsNotOwnerException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemStorage;
    private final CommentRepository commentStorage;
    private final UserService userService;

    private final BookingRepository bookingStorage;

    @Autowired
    public ItemServiceImpl(
            ItemRepository itemStorage,
            CommentRepository commentStorage,
            UserService userService,
            BookingRepository bookingStorage
    ) {
        this.itemStorage = itemStorage;
        this.commentStorage = commentStorage;
        this.userService = userService;
        this.bookingStorage = bookingStorage;
    }

    @Override
    public Item createItem(long userId, Item newItem) {
        User owner = userService.getUserById(userId);
        newItem.setOwner(owner);

        return itemStorage.save(newItem);
    }

    @Override
    public Comment addComment(long itemId, long userId, Comment newComment) {
        User user = userService.getUserById(userId);
        Item item = getItemById(itemId);
        Collection<Booking> finishedBookings = bookingStorage.getBookingsByBookerAndItemAndEndIsBeforeAndStatus(
                user, item, LocalDateTime.now(), BookingStatus.APPROVED
        );

        if (finishedBookings.size() == 0) {
            throw new CannotLeaveCommentException(userId, itemId);
        }

        newComment.setItem(item);
        newComment.setAuthor(user);
        newComment.setCreated(LocalDateTime.now());

        return commentStorage.save(newComment);
    }

    @Override
    public Collection<Comment> getCommentsByItem(Item item) {
        return commentStorage.getCommentsByItem(item);
    }

    @Override
    public Item updateItem(long itemId, long userId, Item newItem) {
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
            validateAndSetName(newItem.getName(), patchedItem);
        }
        if (newItem.getDescription() != null) {
            validateAndSetDescription(newItem.getDescription(), patchedItem);
        }
        if (newItem.getAvailable() != null) {
            validateAndSetIsAvailable(newItem.getAvailable(), patchedItem);
        }

        return itemStorage.save(patchedItem);
    }

    @Override
    public Item getItemById(long itemId) {
        return itemStorage.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));
    }

    @Override
    public Collection<Item> getUserItems(long userId) {
        User user = userService.getUserById(userId);
        return itemStorage.findItemsByOwnerId(user.getId());
    }

    @Override
    public Collection<Item> searchItems(String text) {
        if (text.isBlank()) {
            return List.of();
        }

        return itemStorage.searchItems(text.toLowerCase());
    }

    private void validateAndSetName(@Valid String name, Item item) {
        item.setName(name);
    }

    private void validateAndSetDescription(@Valid String description, Item item) {
        item.setDescription(description);
    }

    private void validateAndSetIsAvailable(@Valid Boolean isAvailable, Item item) {
        item.setAvailable(isAvailable);
    }
}
