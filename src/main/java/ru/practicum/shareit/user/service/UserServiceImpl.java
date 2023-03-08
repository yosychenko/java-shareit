package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Map;

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
    public User updateUser(long userId, Map<String, Object> patch) {
        User userToUpdate = userStorage.getUserById(userId);
        User patchedUser = User.builder()
                .id(userToUpdate.getId())
                .name(userToUpdate.getName())
                .email(userToUpdate.getEmail())
                .build();

        // На основе патча составим новое, обновленное представление пользователя,
        // которым мы хотим заменить существующего пользователя
        for (var entry : patch.entrySet()) {
            switch (entry.getKey()) {
                case "email":
                    validateAndSetEmail(entry.getValue().toString(), patchedUser);
                    break;
                case "name":
                    validateAndSetName(entry.getValue().toString(), patchedUser);
                    break;
            }
        }

        return userStorage.updateUser(userId, patchedUser);
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
