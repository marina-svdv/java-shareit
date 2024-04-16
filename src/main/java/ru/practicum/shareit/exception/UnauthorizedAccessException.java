package ru.practicum.shareit.exception;

public class UnauthorizedAccessException extends RuntimeException{

    public UnauthorizedAccessException() {
        super("Unauthorized access to information.");
    }
}
