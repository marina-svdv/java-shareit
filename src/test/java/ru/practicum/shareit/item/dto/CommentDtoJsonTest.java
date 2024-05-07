package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoJsonTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void serializeCommentDtoTest() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Nice item!");
        commentDto.setItemId(1L);
        commentDto.setAuthorId(2L);
        commentDto.setAuthorName("User User");
        commentDto.setCreated(LocalDateTime.of(2024, 5, 1, 12, 0));

        assertThat(this.json.write(commentDto)).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(this.json.write(commentDto)).extractingJsonPathStringValue("$.text").isEqualTo("Nice item!");
        assertThat(this.json.write(commentDto)).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(this.json.write(commentDto)).extractingJsonPathNumberValue("$.authorId").isEqualTo(2);
        assertThat(this.json.write(commentDto)).extractingJsonPathStringValue("$.authorName").isEqualTo("User User");
        assertThat(this.json.write(commentDto)).extractingJsonPathStringValue("$.created").isEqualTo("2024-05-01T12:00:00");
    }

    @Test
    void deserializeCommentDtoTest() throws Exception {
        String content = "{\"id\":1,\"text\":\"Nice item!\",\"itemId\":1,\"authorId\":2,\"authorName\":\"User User\",\"created\":\"2024-05-01T12:00:00\"}";

        assertThat(this.json.parse(content)).isEqualTo(new CommentDto(1L, "Nice item!", 1L, 2L,
                "User User", LocalDateTime.of(2024, 5, 1, 12, 0)));
    }

    @Test
    void validateBlankText() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("");

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(commentDto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Text must not be blank");
    }

    @Test
    void validateTextExceedingMaxLength() {
        String tooLongText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer nec odio. Praesent libero. " +
                "Sed cursus ante dapibus diam. Sed nisi. Nulla quis sem at nibh elementum imperdiet. Duis sagittis ipsum. " +
                "Praesent mauris. Fusce nec tellus sed augue semper porta. Mauris massa. Vestibulum lacinia arcu eget " +
                "nulla. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos.";

        CommentDto commentDto = new CommentDto();
        commentDto.setText(tooLongText);
        Set<ConstraintViolation<CommentDto>> violations = validator.validate(commentDto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Text must be less than 255 characters");
    }
}
