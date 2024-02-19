package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final Map<String, Long> emails = new HashMap<>();
    private long currentId = 0;

    @Override
    public User create(User user) {
        if (emails.containsKey(user.getEmail())) {
            throw new IllegalArgumentException("A user with this email already exists.");
        }
        user.setId(++currentId);
        users.put(user.getId(), user);
        log.info("Created new user with ID " + user.getId());
        emails.put(user.getEmail(), user.getId());
        log.info("Added new email " + user.getEmail());
        return user;
    }

    @Override
    public User update(Long userId, User updatedUser) {
        User existingUser = users.get(userId);
        if (existingUser == null) {
            throw new IllegalArgumentException("User with id " + updatedUser.getId() + " not found.");
        }
        // изменился ли email на новый и занят ли он
        if (!existingUser.getEmail().equals(updatedUser.getEmail()) && emails.containsKey(updatedUser.getEmail())) {
            throw new IllegalArgumentException("A user with this email already exists.");
        }
        existingUser.setName(updatedUser.getName() != null ? updatedUser.getName() : existingUser.getName());
        if (!existingUser.getEmail().equals(updatedUser.getEmail()) && updatedUser.getEmail() != null) {
            emails.remove(existingUser.getEmail());
            emails.put(updatedUser.getEmail(), updatedUser.getId());
            existingUser.setEmail(updatedUser.getEmail());
        }
        users.put(userId, existingUser);
        return existingUser;
    }

    @Override
    public User findById(Long id) {
        return users.get(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void delete(Long userId) {
        User existingUser = users.get(userId);
        emails.remove(existingUser.getEmail());
        users.remove(userId);
    }
}
