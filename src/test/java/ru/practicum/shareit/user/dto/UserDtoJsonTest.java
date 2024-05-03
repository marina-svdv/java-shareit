package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.context.annotation.Import;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@Import(UserDto.class)
public class UserDtoJsonTest {

    @Autowired
    private JacksonTester<UserDto> json;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void serializeUserDtoTest() throws Exception {
        UserDto userDto = new UserDto(1L, "User User", "user.user@example.com");
        assertThat(this.json.write(userDto)).extractingJsonPathStringValue("$.name").isEqualTo("User User");
        assertThat(this.json.write(userDto)).extractingJsonPathStringValue("$.email").isEqualTo("user.user@example.com");
        assertThat(this.json.write(userDto)).extractingJsonPathNumberValue("$.id").isEqualTo(1);
    }

    @Test
    void deserializeUserDtoTest() throws Exception {
        String content = "{\"id\":1,\"name\":\"User User\",\"email\":\"user.user@example.com\"}";
        assertThat(this.json.parse(content)).usingRecursiveComparison()
                .isEqualTo(new UserDto(1L, "User User", "user.user@example.com"));
    }

    @Test
    void validationErrorWhenNameIsBlank() {
        UserDto userDto = new UserDto(1L, "", "user.user@example.com");
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Name must not be blank");
    }

    @Test
    void validationErrorWhenEmailIsInvalid() {
        UserDto userDto = new UserDto(1L, "User User", "not-email");
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("должно иметь формат адреса электронной почты");
    }

    @Test
    void validationErrorWhenEmailIsBlank() {
        UserDto userDto = new UserDto(1L, "User User", "");
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Email must not be blank");
    }
}
