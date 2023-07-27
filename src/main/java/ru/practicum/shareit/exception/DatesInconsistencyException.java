package ru.practicum.shareit.exception;

public class DatesInconsistencyException extends RuntimeException {
    public DatesInconsistencyException(String message) {
        super(message);
    }
}
