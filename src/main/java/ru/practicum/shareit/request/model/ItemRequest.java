package ru.practicum.shareit.request.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemRequest {
    Long id;
    String description;
    Long authorId;
    LocalDateTime date;
}
