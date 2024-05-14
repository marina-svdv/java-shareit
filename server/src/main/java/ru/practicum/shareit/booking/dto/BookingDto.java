package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    Long id;
    Long itemId;
    ItemDto item;
    Long ownerId;
    Long bookerId;
    UserDto booker;
    LocalDateTime start;
    LocalDateTime end;
    Status status;
}
