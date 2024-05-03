package ru.practicum.shareit.user.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.model.User;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testToUserDto() {
        User user = new User(1L, "User User", "user.user@example.com");
        UserDto userDto = userMapper.toUserDto(user);
        assertNotNull(userDto);
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    public void testToUser() {
        UserDto userDto = new UserDto(1L, "User User", "user.user@example.com");
        User user = userMapper.toUser(userDto);
        assertNotNull(user);
        assertNull(user.getId());
        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());
    }

    @Test
    public void testToUserWithId() {
        UserDto userDto = new UserDto(null, "User User", "user.user@example.com");
        Long userId = 1L;
        User user = userMapper.toUser(userDto, userId);
        assertNotNull(user);
        assertEquals(userId, user.getId());
        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());
    }

    @Test
    public void testToUserDtoWithNullUser() {
        assertThrows(IllegalArgumentException.class, () -> {
            userMapper.toUserDto(null);
        });
    }

    @Test
    public void testToUserWithNullUserDto() {
        assertThrows(IllegalArgumentException.class, () -> {
            userMapper.toUser(null);
        });
    }

    @Test
    public void testToUserWithNullUserDtoAndId() {
        Long userId = 1L;
        assertThrows(IllegalArgumentException.class, () -> {
            userMapper.toUser(null, userId);
        });
    }
}
