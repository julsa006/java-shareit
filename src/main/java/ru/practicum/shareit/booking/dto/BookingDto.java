package ru.practicum.shareit.booking.dto;

import lombok.Value;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

@Value
public class BookingDto {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    Long item;
    Long booker;
    BookingStatus status;
}
