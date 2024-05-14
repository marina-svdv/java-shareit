package ru.practicum.shareit.exception;

public class InvalidPaginationParameterException extends RuntimeException {
    public InvalidPaginationParameterException(String message) {
        super(message);
    }
}
