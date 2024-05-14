package ru.practicum.shareit.exception;

public class ItemRequestNotFoundException  extends RuntimeException {

    public ItemRequestNotFoundException() {
        super("This request does not exist.");
    }
}
