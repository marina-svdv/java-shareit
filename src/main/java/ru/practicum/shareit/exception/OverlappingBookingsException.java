package ru.practicum.shareit.exception;

public class OverlappingBookingsException extends RuntimeException {

    public OverlappingBookingsException (String m) {
        super(m);
    }
}
