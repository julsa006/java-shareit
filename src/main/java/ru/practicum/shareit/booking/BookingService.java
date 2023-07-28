package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.CreateBookingDto;

import java.util.List;

public interface BookingService {
    Booking create(CreateBookingDto booking, Long userId);

    Booking approve(Long bookingId, boolean approved, Long userId);

    List<Booking> getUserBookings(String stringState, Long userId, int from, int size);

    List<Booking> getOwnerBookings(String stringState, Long ownerId, int from, int size);

    Booking get(Long bookingId, Long userId);
}
