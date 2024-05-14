package ru.practicum.shareit.exception;

public class BookingNotFoundException extends RuntimeException {

    public BookingNotFoundException() {
        super("This booking does not exist.");
    }
}
