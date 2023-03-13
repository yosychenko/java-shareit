package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import javax.validation.Valid;
import java.util.Collection;

@Component
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public User createUser(User newUser) {
        return userStorage.createUser(newUser);
    }

    @Override
    public User updateUser(long userId, UserDto newUser) {
        User userToUpdate = userStorage.getUserById(userId);
        User patchedUser = User.builder()
                .id(userToUpdate.getId())
                .name(userToUpdate.getName())
                .email(userToUpdate.getEmail())
                .build();

        if (newUser.getEmail() != null) {
            validateAndSetEmail(newUser.getEmail(), patchedUser);
        }
        if (newUser.getName() != null) {
            validateAndSetName(newUser.getName(), patchedUser);
        }

        return userStorage.updateUser(patchedUser);
    }

    @Override
    public void deleteUser(long userId) {
        userStorage.deleteUser(userId);
    }

    @Override
    public User getUserById(long userId) {
        return userStorage.getUserById(userId);
    }

    @Override
    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    private void validateAndSetEmail(@Valid String email, User user) {
        user.setEmail(email);
    }

    private void validateAndSetName(@Valid String name, User user) {
        user.setName(name);
    }
}
