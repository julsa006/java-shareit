package ru.practicum.shareit.exception;

public class NotUniqueEmailException extends RuntimeException {
    public NotUniqueEmailException(String email) {
        super(String.format("Email %s is not unique", email));
    }
}
