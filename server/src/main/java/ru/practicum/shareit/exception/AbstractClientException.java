package ru.practicum.shareit.exception;

public abstract class AbstractClientException extends RuntimeException {
    AbstractClientException(String message) {
        super(message);
    }
}
