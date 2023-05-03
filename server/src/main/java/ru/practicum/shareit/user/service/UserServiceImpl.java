package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Collection;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userStorage;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userStorage = userRepository;
    }

    @Override
    public User createUser(User newUser) {
        return userStorage.save(newUser);
    }

    @Override
    public User updateUser(long userId, User newUser) {
        User userToUpdate = getUserById(userId);
        User patchedUser = new User();
        patchedUser.setId(userToUpdate.getId());
        patchedUser.setName(userToUpdate.getName());
        patchedUser.setEmail(userToUpdate.getEmail());

        if (newUser.getEmail() != null) {
            patchedUser.setEmail(newUser.getEmail());
        }
        if (newUser.getName() != null) {
            patchedUser.setName(newUser.getName());
        }

        return userStorage.save(patchedUser);
    }

    @Override
    public void deleteUser(long userId) {
        userStorage.deleteById(userId);
    }

    @Override
    public User getUserById(long userId) {
        return userStorage.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }

    @Override
    public Collection<User> getAllUsers() {
        return userStorage.findAll();
    }
}
