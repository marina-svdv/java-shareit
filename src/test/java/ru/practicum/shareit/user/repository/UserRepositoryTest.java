package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void saveUserShouldPersistUser() {
        User user = new User(null, "Test User", "test@example.com");
        User savedUser = userRepository.save(user);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("Test User");
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    public void updateUserShouldUpdateExistingUser() {
        User user = new User(null, "Test User", "test@example.com");
        User savedUser = entityManager.persistFlushFind(user);
        savedUser.setName("Updated Name");
        userRepository.save(savedUser);
        User updatedUser = userRepository.findById(savedUser.getId()).orElse(null);
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
    }

    @Test
    public void deleteUserShouldRemoveUser() {
        User user = new User(null, "Test User", "test@example.com");
        User savedUser = entityManager.persistFlushFind(user);
        userRepository.deleteById(savedUser.getId());
        Optional<User> deletedUser = userRepository.findById(savedUser.getId());
        assertThat(deletedUser).isEmpty();
    }

    @Test
    public void findUserByIdShouldReturnUser() {
        User user = new User(null, "Test User", "test@example.com");
        User savedUser = entityManager.persistFlushFind(user);
        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        assertThat(foundUser).isNotEmpty();
        assertThat(foundUser.get()).isEqualTo(savedUser);
    }

    @Test
    public void saveUserWithDuplicateEmailShouldThrowException() {
        User user1 = new User(null, "Test User", "test@example.com");
        User user2 = new User(null, "Another User", "test@example.com");
        userRepository.save(user1);
        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(user2);
        });
    }

    @Test
    public void isUserExistWhenUserExistsShouldReturnTrue() {
        User user = new User(null, "User User", "user@example.com");
        entityManager.persistAndFlush(user);
        boolean exists = userRepository.existsById(user.getId());
        assertThat(exists).isTrue();
    }

    @Test
    public void isUserExistWhenUserDoesNotExistShouldReturnFalse() {
        long nonexistentUserId = -1;
        boolean exists = userRepository.existsById(nonexistentUserId);
        assertThat(exists).isFalse();
    }
}