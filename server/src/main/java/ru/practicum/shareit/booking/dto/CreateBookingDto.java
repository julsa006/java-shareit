package ru.practicum.shareit.booking.dto;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class CreateBookingDto {
    LocalDateTime start;
    LocalDateTime end;
    Long itemId;
}
