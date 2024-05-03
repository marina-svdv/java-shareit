package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void serializeItemDtoTest() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Drill");
        itemDto.setDescription("Powerful tool");
        itemDto.setAvailable(true);

        assertThat(this.json.write(itemDto)).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(this.json.write(itemDto)).extractingJsonPathStringValue("$.name").isEqualTo("Drill");
        assertThat(this.json.write(itemDto)).extractingJsonPathStringValue("$.description").isEqualTo("Powerful tool");
        assertThat(this.json.write(itemDto)).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
    }

    @Test
    void deserializeItemDtoTest() throws Exception {
        String content = "{\"id\":1,\"name\":\"Drill\",\"description\":\"Powerful tool\",\"available\":true}";

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Drill");
        itemDto.setDescription("Powerful tool");
        itemDto.setAvailable(true);

        assertThat(this.json.parse(content)).usingRecursiveComparison()
                .isEqualTo(itemDto);
    }

    @Test
    void validationErrorWhenNameIsBlank() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setDescription("Powerful tool");
        itemDto.setAvailable(true);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Name must not be blank");
    }

    @Test
    void validationErrorWhenDescriptionIsTooLong() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Drill");
        itemDto.setDescription("Lorem ipsum dolor sit amet," +
                " consectetur adipiscing elit. " +
                "Donec vitae nisi sit amet massa aliquet tristique. " +
                "Sed suscipit sapien nec est luctus, sit amet maximus arcu sodales. " +
                "Integer nec varius nunc, eget tincidunt mi. Integer porttitor, orci id lacinia vestibulum," +
                " leo magna hendrerit velit, a dictum nibh enim nec turpis. " +
                "In id nisi vitae quam aliquam bibendum eu vel eros. " +
                "Mauris vel viverra enim. Vivamus nec nisl magna.");
        itemDto.setAvailable(true);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Description must be less than 200 characters");
    }

    @Test
    void validationErrorWhenDescriptionIsBlank() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Drill");
        itemDto.setAvailable(true);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Description must not be blank");
    }

    @Test
    void validationErrorWhenAvailableIsNull() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Drill");
        itemDto.setDescription("Powerful tool");

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("must not be null");
    }
}
