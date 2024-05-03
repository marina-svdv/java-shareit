package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.booking.model.Status;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@Import(BookingDto.class)
public class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void serializeBookingDtoTest() throws Exception {
        BookingDto bookingDto = new BookingDto(1L, 2L, null, 3L, 4L, null,
                LocalDateTime.now(), LocalDateTime.now().plusDays(1), Status.APPROVED);
        assertThat(this.json.write(bookingDto)).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(this.json.write(bookingDto)).extractingJsonPathNumberValue("$.itemId").isEqualTo(2);
        assertThat(this.json.write(bookingDto)).extractingJsonPathNumberValue("$.ownerId").isEqualTo(3);
        assertThat(this.json.write(bookingDto)).extractingJsonPathNumberValue("$.bookerId").isEqualTo(4);
        assertThat(this.json.write(bookingDto)).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
    }

    @Test
    void deserializeBookingDtoTest() throws Exception {
        String content = "{\"id\":1,\"itemId\":2,\"ownerId\":3,\"bookerId\":4,\"start\":\"2023-09-01T12:00:00\"," +
                "\"end\":\"2023-09-02T12:00:00\",\"status\":\"APPROVED\"}";
        assertThat(this.json.parseObject(content).getId()).isEqualTo(1);
    }

    @Test
    void validationErrorWhenStartTimeIsPast() {
        BookingDto bookingDto = new BookingDto(1L, 2L, null, 3L, 4L, null,
                LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), Status.APPROVED);
        Set<ConstraintViolation<BookingDto>> violations = validator.validate(bookingDto);
        assertThat(violations).isNotEmpty();
    }

    @Test
    void validationErrorWhenEndTimeIsBeforeStartTime() {
        BookingDto bookingDto = new BookingDto(1L, 2L, null, 3L, 4L, null,
                LocalDateTime.now().plusDays(1), LocalDateTime.now(), Status.APPROVED);
        Set<ConstraintViolation<BookingDto>> violations = validator.validate(bookingDto);
        assertThat(violations).isNotEmpty();
    }

    @Test
    void validationErrorWhenItemIdIsNull() {
        BookingDto bookingDto = new BookingDto(1L, null, null, 3L, 4L, null,
                LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(1), Status.APPROVED);
        Set<ConstraintViolation<BookingDto>> violations = validator.validate(bookingDto);
        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Item ID must not be null");
    }

    @Test
    void validationErrorWhenStartOrEndTimeIsNull() {
        BookingDto bookingDto = new BookingDto(1L, 2L, null, 3L, 4L, null,
                null, null, Status.APPROVED);
        Set<ConstraintViolation<BookingDto>> violations = validator.validate(bookingDto);
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream().anyMatch(v -> v.getMessage().contains("must not be null"))).isTrue();
    }
}
