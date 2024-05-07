package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.context.annotation.Import;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@Import(ItemRequestDto.class)
public class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void serializeItemRequestDtoTest() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "Need a laptop", 2L,
                LocalDateTime.now(), List.of());

        assertThat(this.json.write(itemRequestDto)).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(this.json.write(itemRequestDto)).extractingJsonPathStringValue("$.description")
                .isEqualTo("Need a laptop");
        assertThat(this.json.write(itemRequestDto)).extractingJsonPathNumberValue("$.requesterId")
                .isEqualTo(2);
    }

    @Test
    void deserializeItemRequestDtoTest() throws Exception {
        String content = "{\"id\":1,\"description\":\"Need a laptop\",\"requesterId\":2,\"" +
                "created\":\"2023-09-01T12:00:00\"}";
        ItemRequestDto parsed = this.json.parseObject(content);
        assertThat(parsed.getId()).isEqualTo(1);
        assertThat(parsed.getDescription()).isEqualTo("Need a laptop");
        assertThat(parsed.getRequesterId()).isEqualTo(2);
        assertThat(parsed.getCreated()).isEqualTo(LocalDateTime.of(2023, 9, 1, 12, 0));
    }

    @Test
    void validationErrorWhenDescriptionIsBlank() {
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "", 2L,
                LocalDateTime.now(), List.of());
        Set<ConstraintViolation<ItemRequestDto>> violations = validator.validate(itemRequestDto);
        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage()).contains("must not be blank");
    }
}
