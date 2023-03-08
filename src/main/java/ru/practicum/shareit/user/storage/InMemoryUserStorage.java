package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.exception.DuplicateEmailException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> storage;
    private long idCounter;

    public InMemoryUserStorage() {
        this.storage = new HashMap<>();
    }

    @Override
    public User createUser(User newUser) {
        if (isDuplicateEmail(newUser)) {
            throw new DuplicateEmailException(newUser.getEmail());
        }

        newUser.setId(++idCounter);
        storage.put(newUser.getId(), newUser);

        return getUserById(newUser.getId());
    }

    @Override
    public User updateUser(long userId, User newUser) {
        if (isDuplicateEmail(newUser)) {
            throw new DuplicateEmailException(newUser.getEmail());
        }
        storage.put(newUser.getId(), newUser);

        return getUserById(newUser.getId());
    }

    @Override
    public void deleteUser(long userId) {
        User userToDelete = getUserById(userId);
        storage.remove(userToDelete.getId());
    }

    @Override
    public User getUserById(long userId) {
        User user = storage.get(userId);

        if (user == null) {
            throw new UserNotFoundException(userId);
        }

        return user;
    }

    @Override
    public Collection<User> getAllUsers() {
        return storage.values();
    }

    private boolean isDuplicateEmail(User newUser) {
        for (var entry : storage.entrySet()) {
            String existingEmail = entry.getValue().getEmail();
            long existingId = entry.getValue().getId();
            if (existingEmail.equals(newUser.getEmail()) && existingId != newUser.getId()) {
                return true;
            }
        }
        return false;
    }
}
