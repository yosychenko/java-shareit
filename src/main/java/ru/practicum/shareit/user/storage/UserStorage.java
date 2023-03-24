package ru.practicum.shareit.user.storage;


import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserStorage {
    User createUser(User newUser);

    User updateUser(User newUser);

    void deleteUser(long userId);

    User getUserById(long userId);

    Collection<User> getAllUsers();
}
