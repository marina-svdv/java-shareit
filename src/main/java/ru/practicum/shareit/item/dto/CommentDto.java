package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long id;

    @Size(max = 255, message = "Text must be less than 255 characters")
    @NotBlank(message = "Text must not be blank")
    private String text;
    private Long itemId;
    private Long authorId;
    private String authorName;
    private LocalDateTime created;
}
