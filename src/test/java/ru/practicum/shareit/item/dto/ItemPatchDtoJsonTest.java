package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemPatchDtoJsonTest {

    @Autowired
    private JacksonTester<ItemPatchDto> json;

    @Test
    void serializeItemPatchDtoTest() throws Exception {
        ItemPatchDto patchDto = new ItemPatchDto();
        patchDto.setName("Drill");
        patchDto.setDescription("Powerful tool");
        patchDto.setAvailable(true);

        assertThat(this.json.write(patchDto)).extractingJsonPathStringValue("$.name").isEqualTo("Drill");
        assertThat(this.json.write(patchDto)).extractingJsonPathStringValue("$.description").isEqualTo("Powerful tool");
        assertThat(this.json.write(patchDto)).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
    }

    @Test
    void deserializeItemPatchDtoTest() throws Exception {
        String content = "{\"name\":\"Drill\",\"description\":\"Powerful tool\",\"available\":true}";
        ObjectMapper objectMapper = new ObjectMapper();
        ItemPatchDto patchDto = objectMapper.readValue(content, ItemPatchDto.class);

        assertThat(patchDto.getName()).isEqualTo("Drill");
        assertThat(patchDto.getDescription()).isEqualTo("Powerful tool");
        assertThat(patchDto.getAvailable()).isEqualTo(true);
    }
}
