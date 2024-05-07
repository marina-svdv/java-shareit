package ru.practicum.shareit.user.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

@Component
public class UserMapper {

    public UserDto toUserDto(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Переданный объект user не может быть null");
        }
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }

    public User toUser(UserDto userDto) {
        if (userDto == null) {
            throw new IllegalArgumentException("UserDto object cannot be null");
        }
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }

    public User toUser(UserDto userDto, Long userId) {
        if (userDto == null) {
            throw new IllegalArgumentException("UserDto object cannot be null");
        }
        if (userId == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        User user = new User();
        user.setId(userId);
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }
}
