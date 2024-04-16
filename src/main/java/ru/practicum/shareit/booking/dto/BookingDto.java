package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.validation.ValidBookingDates;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@ValidBookingDates
public class BookingDto {
    Long id;

    @NotNull(message = "Item ID must not be null")
    Long itemId;
    ItemDto item;
    Long ownerId;
    Long bookerId;
    UserDto booker;

    @NotNull(message = "Start time must not be null")
    @FutureOrPresent(message = "Start time must be in the future or present")
    LocalDateTime start;

    @NotNull(message = "End time must not be null")
    @FutureOrPresent(message = "End time must be in the future or present")
    LocalDateTime end;
    Status status;
}
