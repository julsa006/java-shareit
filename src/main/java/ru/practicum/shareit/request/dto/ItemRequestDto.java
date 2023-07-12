package ru.practicum.shareit.request.dto;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class ItemRequestDto {
    Long id;
    String description;
    Long requestor;
    LocalDateTime created;
}
