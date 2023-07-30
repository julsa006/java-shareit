package ru.practicum.shareit.fixtures;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

public class BookingFixture {
    private static Long idSequence = 1L;

    public static Booking getActualBooking(User user, Item item) {
        return getActualBooking(idSequence++, user, item);
    }

    public static Booking getActualBooking(Long id, User user, Item item) {
        LocalDateTime now = LocalDateTime.now();
        return new Booking(
                id,
                now.minusDays(1),
                now.plusDays(1),
                item,
                user,
                BookingStatus.APPROVED
        );
    }

    public static Booking getPastBooking(Long id, User user, Item item) {
        LocalDateTime now = LocalDateTime.now();
        return new Booking(
                id,
                now.minusDays(2),
                now.minusDays(1),
                item,
                user,
                BookingStatus.APPROVED
        );
    }

    public static Booking getFutureBooking(Long id, User user, Item item) {
        LocalDateTime now = LocalDateTime.now();
        return new Booking(
                id,
                now.plusDays(1),
                now.plusDays(2),
                item,
                user,
                BookingStatus.APPROVED
        );
    }
}
