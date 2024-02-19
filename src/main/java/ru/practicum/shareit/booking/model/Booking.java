package ru.practicum.shareit.booking.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Booking {
    Long id;
    Long itemId;
    Long bookerId;
    LocalDateTime start;
    LocalDateTime end;
    Status status;
}
