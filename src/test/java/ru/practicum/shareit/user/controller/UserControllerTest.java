package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper userMapper;

    @Test
    void createUserShouldReturnUserDtoWhenValidRequest() throws Exception {
        User validUser = new User(1L, "Updated User", "update@example.com");
        UserDto validUserDto = new UserDto(1L, "Updated User", "update@example.com");

        when(userMapper.toUser(any(UserDto.class))).thenReturn(validUser);
        when(userService.saveUser(any(User.class))).thenReturn(validUser);
        when(userMapper.toUserDto(any(User.class))).thenReturn(validUserDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated User"))
                .andExpect(jsonPath("$.email").value("update@example.com"));

        verify(userService).saveUser(any(User.class));
        verify(userMapper).toUserDto(any(User.class));
    }

    @Test
    void createUserShouldReturnBadRequestWhenInvalidRequest() throws Exception {
        UserDto validUserDto = new UserDto(1L, "Updated User", "update@example.com");
        validUserDto.setEmail("");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUserDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUserShouldReturnUpdatedUserDtoWhenValidRequest() throws Exception {
        Long userId = 1L;
        UserDto inputUserDto = new UserDto(userId, "Updated Name", "update@example.com");
        User inputUser = new User(userId, "Updated Name", "update@example.com");
        UserDto returnedUserDto = new UserDto(userId, "Updated Name", "update@example.com");

        when(userMapper.toUser(any(UserDto.class), eq(userId))).thenReturn(inputUser);
        when(userService.updateUser(eq(userId), any(User.class))).thenReturn(inputUser);
        when(userMapper.toUserDto(any(User.class))).thenReturn(returnedUserDto);

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputUserDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("update@example.com"));

        verify(userService).updateUser(eq(userId), any(User.class));
        verify(userMapper).toUserDto(any(User.class));
    }

    @Test
    void updateUserShouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        Long userId = 1L;
        UserDto inputUserDto = new UserDto(userId, "Nonexistent User", "nonexistent@example.com");
        User inputUser = new User(userId, "Nonexistent User", "nonexistent@example.com");

        when(userMapper.toUser(any(UserDto.class), eq(userId))).thenReturn(inputUser);
        when(userService.updateUser(eq(userId), any(User.class))).thenThrow(new UserNotFoundException());

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputUserDto)))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(userService).updateUser(eq(userId), any(User.class));
    }

    @Test
    void getUserByIdShouldReturnUserWhenUserExists() throws Exception {
        Long userId = 1L;
        UserDto userDto = new UserDto(userId, "Existing User", "existing@example.com");
        User user = new User(userId, "Existing User", "existing@example.com");

        when(userService.getUserById(eq(userId))).thenReturn(user);
        when(userMapper.toUserDto(any(User.class))).thenReturn(userDto);

        mockMvc.perform(get("/users/{userId}", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Existing User"))
                .andExpect(jsonPath("$.email").value("existing@example.com"));

        verify(userService).getUserById(userId);
        verify(userMapper).toUserDto(user);
    }

    @Test
    void getUserByIdShouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        Long userId = 1L;

        when(userService.getUserById(eq(userId))).thenThrow(new UserNotFoundException());

        mockMvc.perform(get("/users/{userId}", userId))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(userService).getUserById(userId);
    }

    @Test
    void getAllUsersShouldReturnAllUsers() throws Exception {
        UserDto userDto1 = new UserDto(1L, "User One", "user1@example.com");
        UserDto userDto2 = new UserDto(2L, "User Two", "user2@example.com");
        List<UserDto> userDtos = List.of(userDto1, userDto2);
        User user1 = new User(1L, "User One", "user1@example.com");
        User user2 = new User(2L, "User Two", "user2@example.com");
        List<User> users = List.of(user1, user2);

        when(userService.getAllUsers()).thenReturn(users);
        when(userMapper.toUserDto(any(User.class)))
                .thenReturn(userDto1)
                .thenReturn(userDto2);

        mockMvc.perform(get("/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(userDto1.getId()))
                .andExpect(jsonPath("$[0].name").value(userDto1.getName()))
                .andExpect(jsonPath("$[0].email").value(userDto1.getEmail()))
                .andExpect(jsonPath("$[1].id").value(userDto2.getId()))
                .andExpect(jsonPath("$[1].name").value(userDto2.getName()))
                .andExpect(jsonPath("$[1].email").value(userDto2.getEmail()));

        verify(userService).getAllUsers();
        verify(userMapper, times(users.size())).toUserDto(any(User.class));
    }

    @Test
    void deleteValidUserShouldRemoveUser() throws Exception {
        Long userId = 1L;

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService).deleteUser(userId);
    }

    @Test
    void deleteUserShouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        Long userId = 999L;

        doThrow(new UserNotFoundException())
                .when(userService).deleteUser(userId);

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isNotFound());

        verify(userService).deleteUser(userId);
    }
}
