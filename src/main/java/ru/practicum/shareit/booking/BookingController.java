package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.exception.DatesException;
import ru.practicum.shareit.exception.UnsupportedException;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {
    BookingService bookingService;

    @PostMapping
    public BookingDto create(@Valid @RequestBody CreateBookingDto booking,
                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        validateDates(booking.getStart(), booking.getEnd());
        return BookingMapper.toBookingDto(bookingService.create(booking, userId));
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@PathVariable Long bookingId, @RequestParam boolean approved,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return BookingMapper.toBookingDto(bookingService.approve(bookingId, approved, userId));
    }

    @GetMapping("/{bookingId}")
    public BookingDto get(@PathVariable Long bookingId,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        return BookingMapper.toBookingDto(bookingService.get(bookingId, userId));
    }


    @GetMapping
    public List<BookingDto> getUserBookings(@RequestParam(name = "state", defaultValue = "ALL") String stringState,
                                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        State state;
        try {
            state = State.valueOf(stringState);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedException(String.format("Unknown state: %s", stringState));
        }
        return bookingService.getUserBookings(state, userId).stream()
                .map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestParam(name = "state", defaultValue = "ALL") String stringState,
                                             @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        State state;
        try {
            state = State.valueOf(stringState);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedException(String.format("Unknown state: %s", stringState));
        }
        return bookingService.getOwnerBookings(state, ownerId).stream()
                .map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    private void validateDates(LocalDateTime start, LocalDateTime end) {
        if (LocalDateTime.now().isAfter(end)) {
            throw new DatesException("End date cannot be in the past");
        }
        if (LocalDateTime.now().isAfter(start)) {
            throw new DatesException("Start date cannot be in the past");
        }
        if (!start.isBefore(end)) {
            throw new DatesException("Start date must be before end date");
        }
    }

}
