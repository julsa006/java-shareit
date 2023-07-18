package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.Booking;

import java.util.List;

@Data
@AllArgsConstructor
public class FullItem {
    Item item;
    Booking lastBooking;
    Booking nextBooking;
    List<Comment> comments;
}
