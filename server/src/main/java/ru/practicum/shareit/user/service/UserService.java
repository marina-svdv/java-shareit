package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import java.util.List;

public interface UserService {

    UserDto saveUser(UserDto userDto);

    UserDto updateUser(Long userId, UserDto updatedUser);

    UserDto getUserById(Long userId);

    List<UserDto> getAllUsers();

    void deleteUser(Long userId);

    boolean isUserExist(long bookingId);
}
