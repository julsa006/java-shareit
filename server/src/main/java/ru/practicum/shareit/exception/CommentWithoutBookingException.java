package ru.practicum.shareit.exception;

public class CommentWithoutBookingException extends AbstractClientException {
    public CommentWithoutBookingException(String message) {
        super(message);
    }
}
