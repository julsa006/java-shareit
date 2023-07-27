package ru.practicum.shareit.booking.dto;

import lombok.Value;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Value
public class CreateBookingDto {
    @NotNull
    LocalDateTime start;
    @NotNull
    LocalDateTime end;
    @NotNull
    Long itemId;
}
