package ru.practicum.shareit.item.dto;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class CommentMapperTest {

    @Autowired
    CommentMapper commentMapper;

    @Test
    public void testToCommentDto() {
        User author = new User();
        author.setId(1L);
        author.setName("Test Author");
        author.setEmail("author@example.com");
        Item item = new Item();
        item.setId(1L);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Test Comment");
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        CommentDto commentDto = commentMapper.toCommentDto(comment);

        assertNotNull(commentDto);
        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(comment.getItem().getId(), commentDto.getItemId());
        assertEquals(comment.getAuthor().getId(), commentDto.getAuthorId());
        assertEquals(comment.getAuthor().getName(), commentDto.getAuthorName());
        assertEquals(comment.getCreated(), commentDto.getCreated());
    }

    @Test
    public void testToComment() {
        User author = new User();
        author.setId(1L);
        Item item = new Item();
        item.setId(1L);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Test Comment");
        commentDto.setAuthorId(author.getId());
        commentDto.setItemId(item.getId());

        Comment comment = commentMapper.toComment(commentDto, item, author);

        assertNotNull(comment);
        assertNull(comment.getId());
        assertEquals(commentDto.getText(), comment.getText());
        assertEquals(item, comment.getItem());
        assertEquals(author, comment.getAuthor());
        assertNotNull(comment.getCreated());
    }
}
