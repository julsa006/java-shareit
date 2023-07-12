package ru.practicum.shareit.exception;

import lombok.Value;

@Value
public class ErrorResponse {
    private final String error;
}
