package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.CreateBookingDto;

import java.util.List;

public interface BookingService {
    Booking create(CreateBookingDto booking, Long userId);

    Booking approve(Long bookingId, boolean approved, Long userId);

    List<Booking> getUserBookings(State state, Long userId);

    List<Booking> getOwnerBookings(State state, Long ownerId);

    Booking get(Long bookingId, Long userId);
}
