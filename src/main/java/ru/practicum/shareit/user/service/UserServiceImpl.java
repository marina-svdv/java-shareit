package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User createUser(User user) {
        return userRepository.create(user);
    }

    @Override
    public User updateUser(Long userId, User updatedUser) {
        return userRepository.update(userId, updatedUser);
    }

    @Override
    public User getUserById(Long userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new UserNotFoundException();
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(Long userId) {
        if (userRepository.findById(userId) == null) {
            throw new IllegalArgumentException("User with id " + userId + " not found.");
        }
        userRepository.delete(userId);
    }
}
