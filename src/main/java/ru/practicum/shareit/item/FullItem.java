package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.Booking;

import java.util.List;

@Data
@AllArgsConstructor
public class FullItem {
    private Item item;
    private Booking lastBooking;
    private Booking nextBooking;
    private List<Comment> comments;
}
