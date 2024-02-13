package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {

    User create(User user);

    User update(Long userId, User updatedUser);

    User findById(Long id);

    List<User> findAll();

    void delete(Long userId);
}
