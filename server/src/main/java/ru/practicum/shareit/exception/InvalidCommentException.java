package ru.practicum.shareit.exception;

public class InvalidCommentException extends  RuntimeException {

    public InvalidCommentException() {
        super("User has no rights to comment on this item.");
    }
}
