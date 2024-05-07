package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {

    private Long id;

    @NotBlank(message = "must not be blank")
    private String description;
    private Long requesterId;
    private LocalDateTime created;
    private List<ItemDto> items;
}
