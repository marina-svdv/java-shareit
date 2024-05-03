package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CommentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void saveCommentShouldPersistComment() {
        User author = new User();
        author.setName("Test User");
        author.setEmail("test@example.com");
        User savedAuthor = userRepository.save(author);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(savedAuthor);
        Item savedItem = itemRepository.save(item);

        Comment comment = new Comment();
        comment.setText("Nice item!");
        comment.setCreated(LocalDateTime.now());
        comment.setAuthor(savedAuthor);
        comment.setItem(savedItem);

        Comment savedComment = commentRepository.save(comment);

        assertThat(savedComment).isNotNull();
        assertThat(savedComment.getId()).isNotNull();
        assertThat(savedComment.getText()).isEqualTo("Nice item!");
        assertThat(savedComment.getAuthor()).isEqualTo(savedAuthor);
        assertThat(savedComment.getItem()).isEqualTo(savedItem);
    }

    @Test
    public void updateCommentShouldModifyExistingComment() {
        User author = new User();
        author.setName("Test User");
        author.setEmail("test@example.com");
        User savedAuthor = userRepository.save(author);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(savedAuthor);
        Item savedItem = itemRepository.save(item);

        Comment comment = new Comment();
        comment.setText("Nice item!");
        comment.setCreated(LocalDateTime.now());
        comment.setAuthor(savedAuthor);
        comment.setItem(savedItem);
        Comment savedComment = commentRepository.save(comment);

        String updatedText = "Updated text!";
        savedComment.setText(updatedText);
        Comment updatedComment = commentRepository.save(savedComment);

        assertThat(updatedComment).isNotNull();
        assertThat(updatedComment.getId()).isEqualTo(savedComment.getId());
        assertThat(updatedComment.getText()).isEqualTo(updatedText);
    }

    @Test
    public void findCommentByIdShouldReturnComment() {
        User author = new User();
        author.setName("Test User");
        author.setEmail("test@example.com");
        User savedAuthor = userRepository.save(author);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(savedAuthor);
        Item savedItem = itemRepository.save(item);

        Comment comment = new Comment();
        comment.setText("Nice item!");
        comment.setCreated(LocalDateTime.now());
        comment.setAuthor(savedAuthor);
        comment.setItem(savedItem);
        Comment savedComment = commentRepository.save(comment);

        Optional<Comment> foundComment = commentRepository.findById(savedComment.getId());

        assertThat(foundComment).isPresent();
        assertThat(foundComment.get()).isEqualTo(savedComment);
    }

    @Test
    public void deleteCommentByIdShouldRemoveComment() {
        User author = new User();
        author.setName("Test User");
        author.setEmail("test@example.com");
        User savedAuthor = userRepository.save(author);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(savedAuthor);
        Item savedItem = itemRepository.save(item);

        Comment comment = new Comment();
        comment.setText("Nice item!");
        comment.setCreated(LocalDateTime.now());
        comment.setAuthor(savedAuthor);
        comment.setItem(savedItem);
        Comment savedComment = commentRepository.save(comment);

        commentRepository.deleteById(savedComment.getId());

        Optional<Comment> deletedComment = commentRepository.findById(savedComment.getId());
        assertThat(deletedComment).isEmpty();
    }

    @Test
    public void findCommentsByItemIdShouldReturnListOfComments() {
        User author = new User();
        author.setName("Test User");
        author.setEmail("test@example.com");
        User savedAuthor = userRepository.save(author);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(savedAuthor);
        Item savedItem = itemRepository.save(item);

        Comment comment1 = new Comment();
        comment1.setText("Nice item!");
        comment1.setCreated(LocalDateTime.now());
        comment1.setAuthor(savedAuthor);
        comment1.setItem(savedItem);
        Comment savedComment1 = commentRepository.save(comment1);

        Comment comment2 = new Comment();
        comment2.setText("Great item!");
        comment2.setCreated(LocalDateTime.now());
        comment2.setAuthor(savedAuthor);
        comment2.setItem(savedItem);
        Comment savedComment2 = commentRepository.save(comment2);

        List<Comment> foundComments = commentRepository.findByItemId(savedItem.getId());

        assertThat(foundComments).containsExactly(savedComment1, savedComment2);
    }
}
