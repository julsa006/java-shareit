package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
@Validated
public class BookingController {
    BookingService bookingService;

    @PostMapping
    public BookingDto create(@Valid @RequestBody CreateBookingDto booking,
                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return BookingMapper.toBookingDto(
                bookingService.create(booking.getItemId(), booking.getStart(), booking.getEnd(), userId)
        );
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
    public List<BookingDto> getUserBookings(@RequestParam(name = "state", defaultValue = "ALL") String state,
                                            @RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                            @RequestParam(defaultValue = "10") @Positive int size) {
        return bookingService.getUserBookings(state, userId, from, size).stream()
                .map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestParam(name = "state", defaultValue = "ALL") String state,
                                             @RequestHeader("X-Sharer-User-Id") Long ownerId,
                                             @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                             @RequestParam(defaultValue = "10") @Positive int size) {
        return bookingService.getOwnerBookings(state, ownerId, from, size).stream()
                .map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

}
