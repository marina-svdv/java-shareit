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
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long userId, User updatedUser) {
        User userToUpdate = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        userToUpdate.setEmail(updatedUser.getEmail() != null ? updatedUser.getEmail() : userToUpdate.getEmail());
        userToUpdate.setName(updatedUser.getName() != null ? updatedUser.getName() : userToUpdate.getName());
        return userRepository.save(userToUpdate);
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public boolean isUserExist(long userId) {
        return userRepository.existsById(userId);
    }
}
