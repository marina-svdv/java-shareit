package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(message = "Name must not be blank")
    private String name;
    @Size(max = 200, message = "Description must be less than 200 characters")
    @NotBlank(message = "Description must not be blank")
    private String description;
    @NotNull
    private Boolean available;
    //private Long ownerId;
}
