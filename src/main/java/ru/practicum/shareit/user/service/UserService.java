package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Map;

public interface UserService {

    User createUser(User newUser);

    User updateUser(long userId, Map<String, Object> patch);

    void deleteUser(long userId);

    User getUserById(long userId);

    Collection<User> getAllUsers();
}
