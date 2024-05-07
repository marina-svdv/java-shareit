package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void saveUserWithValidUserShouldReturnSavedUser() {
        User validUser = new User(1L, "User User", "user.user@example.com");
        when(userRepository.save(any(User.class))).thenReturn(validUser);
        User result = userService.saveUser(validUser);

        assertNotNull(result);
        assertEquals(validUser.getId(), result.getId());
        assertEquals(validUser.getName(), result.getName());
        assertEquals(validUser.getEmail(), result.getEmail());
        verify(userRepository).save(validUser);

    }

    @Test
    void saveUserWithNullUserShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> userService.saveUser(null));
    }

    @Test
    void updateUserExistingUserShouldUpdateCorrectly() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setName("Old Name");
        existingUser.setEmail("old@example.com");

        User updatedUser = new User();
        updatedUser.setName("New Name");
        updatedUser.setEmail("new@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        User result = userService.updateUser(1L, updatedUser);

        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals("new@example.com", result.getEmail());
        verify(userRepository).save(existingUser);
    }

    @Test
    void updateUserNonExistingUserShouldThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(1L, new User()));
    }

    @Test
    void updateUserNullFieldsShouldNotUpdateThoseFields() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setName("Old Name");
        existingUser.setEmail("old@example.com");

        User updatedUser = new User();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        User result = userService.updateUser(1L, updatedUser);

        assertEquals("Old Name", result.getName());
        assertEquals("old@example.com", result.getEmail());
    }

    @Test
    void getUserByIdWithValidIdShouldReturnUser() {
        User validUser = new User(1L, "User User", "user.user@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(validUser));

        User result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(validUser.getId(), result.getId());
        assertEquals(validUser.getName(), result.getName());
        assertEquals(validUser.getEmail(), result.getEmail());
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserByIdWithInvalidIdShouldThrowUserNotFoundException() {
        Long invalidUserId = 2L;
        when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(invalidUserId));
        verify(userRepository).findById(invalidUserId);
    }

    @Test
    void getAllUsersShouldReturnAllUsers() {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("User One");
        user1.setEmail("user1@example.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("User Two");
        user2.setEmail("user2@example.com");

        List<User> expectedUsers = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(expectedUsers);

        List<User> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void getAllUsersWhenNoUsersShouldReturnEmptyList() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<User> result = userService.getAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository).findAll();
    }

    @Test
    void deleteUserWithValidIdShouldInvokeDelete() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setName("Test User");
        user.setEmail("test@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        userService.deleteUser(userId);
        verify(userRepository).deleteById(userId);
    }

    @Test
    void isUserExistWithExistingUserShouldReturnTrue() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        boolean result = userService.isUserExist(userId);

        assertTrue(result);
        verify(userRepository).existsById(userId);
    }

    @Test
    void isUserExistWithNotExistingUserShouldReturnFalse() {
        Long userId = 99L;
        when(userRepository.existsById(userId)).thenReturn(false);

        boolean result = userService.isUserExist(userId);

        assertFalse(result);
        verify(userRepository).existsById(userId);
    }
}
