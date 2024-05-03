package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Creating user: {}", userDto.getEmail());
        UserDto createdUser = userMapper.toUserDto(userService.saveUser(userMapper.toUser(userDto)));
        log.info("User created with ID: {}", createdUser.getId());
        return createdUser;
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        log.info("Updating user ID: {}", userId);
        User updatedUser = userService.updateUser(userId, userMapper.toUser(userDto, userId));
        UserDto resultUserDto = userMapper.toUserDto(updatedUser);
        log.info("User updated with ID: {}", resultUserDto.getId());
        return resultUserDto;
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        log.info("Fetching user by ID: {}", userId);
        return userMapper.toUserDto(userService.getUserById(userId));
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Fetching all users");
        return userService.getAllUsers().stream().map(userMapper::toUserDto).collect(Collectors.toList());
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("Deleting user ID: {}", userId);
        userService.deleteUser(userId);
        log.info("User deleted with ID: {}", userId);
    }
}
