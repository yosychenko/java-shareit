package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.exception.UserIsNotOwnerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Map;

@Component
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Autowired
    public ItemServiceImpl(ItemStorage itemStorage, UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    @Override
    public Item createItem(long userId, Item newItem) {
        User owner = userStorage.getUserById(userId);
        newItem.setOwner(owner);

        return itemStorage.createItem(newItem);
    }

    @Override
    public Item updateItem(long itemId, long userId, Map<String, Object> patch) {
        Item itemToUpdate = itemStorage.getItemById(itemId);
        User user = userStorage.getUserById(userId);

        if (itemToUpdate.getOwner().getId() != user.getId()) {
            throw new UserIsNotOwnerException(userId, itemId);
        }

        Item patchedItem = Item.builder()
                .id(itemToUpdate.getId())
                .name(itemToUpdate.getName())
                .description(itemToUpdate.getDescription())
                .available(itemToUpdate.getAvailable())
                .owner(itemToUpdate.getOwner())
                .request(itemToUpdate.getRequest())
                .build();

        for (var entry : patch.entrySet()) {
            switch (entry.getKey()) {
                case "name":
                    validateAndSetName(entry.getValue().toString(), patchedItem);
                    break;
                case "description":
                    validateAndSetDescription(entry.getValue().toString(), patchedItem);
                    break;
                case "available":
                    validateAndSetIsAvailable(Boolean.parseBoolean(entry.getValue().toString()), patchedItem);
                    break;
            }
        }


        return itemStorage.updateItem(patchedItem);
    }

    @Override
    public Item getItemById(long itemId) {
        return itemStorage.getItemById(itemId);
    }

    @Override
    public Collection<Item> getUserItems(long userId) {
        User user = userStorage.getUserById(userId);

        return itemStorage.getUserItems(user.getId());
    }

    @Override
    public Collection<Item> searchItems(String text) {
        return itemStorage.searchItems(text);
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