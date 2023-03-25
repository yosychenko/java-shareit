package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.UserIsNotOwnerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemStorage;
    private final UserRepository userStorage;

    @Autowired
    public ItemServiceImpl(ItemRepository itemStorage, UserRepository userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    @Override
    public Item createItem(long userId, Item newItem) {
        User owner = userStorage.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        newItem.setOwner(owner);

        return itemStorage.save(newItem);
    }

    @Override
    public Item updateItem(long itemId, long userId, ItemDto newItem) {
        Item itemToUpdate = itemStorage.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));
        User user = userStorage.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

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
        User user = userStorage.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        return itemStorage.findItemByOwner(user);
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
