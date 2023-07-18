package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingLiteDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStartTime(),
                booking.getEndTime(),
                ItemMapper.toItemDto(booking.getItem()),
                UserMapper.toUserDto(booking.getBooker()),
                booking.getStatus()
        );
    }

    public static BookingLiteDto toBookingLiteDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return new BookingLiteDto(
                booking.getId(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getBooker().getId(),
                booking.getStatus()
        );
    }
}
