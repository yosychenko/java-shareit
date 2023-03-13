package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {

    User createUser(User newUser);

    User updateUser(long userId, UserDto newUser);

    void deleteUser(long userId);

    User getUserById(long userId);

    Collection<User> getAllUsers();
}
