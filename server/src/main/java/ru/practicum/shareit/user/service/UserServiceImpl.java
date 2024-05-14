package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto saveUser(UserDto userDto) {
        if (userDto == null) {
            throw new IllegalArgumentException("User must not be null");
        }
        User user = userMapper.toUser(userDto);
        user = userRepository.save(user);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto updatedUser) {
        User userToUpdate = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        userToUpdate.setEmail(updatedUser.getEmail() != null ? updatedUser.getEmail() : userToUpdate.getEmail());
        userToUpdate.setName(updatedUser.getName() != null ? updatedUser.getName() : userToUpdate.getName());
        userToUpdate = userRepository.save(userToUpdate);
        return userMapper.toUserDto(userToUpdate);
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        return userMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        userRepository.deleteById(userId);
    }

    @Override
    public boolean isUserExist(long userId) {
        return userRepository.existsById(userId);
    }
}
