package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.exception.DatesInconsistencyException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.UnsupportedOperationException;
import ru.practicum.shareit.utils.DBMockTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringJUnitConfig({BookingServiceImpl.class})
class BookingServiceImplTest extends DBMockTest {
    @Autowired
    BookingServiceImpl bookingService;

    @Test
    void validateDatesCantBeInPast() {
        LocalDateTime startPast = LocalDateTime.now().minusDays(2);
        LocalDateTime endPast = LocalDateTime.now().minusDays(1);
        LocalDateTime endFuture = LocalDateTime.now().plusDays(2);

        assertThrows(DatesInconsistencyException.class, () -> {
            bookingService.validateDates(startPast, endFuture);
        });
        assertThrows(DatesInconsistencyException.class, () -> {
            bookingService.validateDates(startPast, endPast);
        });
    }

    @Test
    void validateDatesStartShouldBeBeforeEnd() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);

        assertThrows(DatesInconsistencyException.class, () -> {
            bookingService.validateDates(start, start.minusDays(1));
        });
        assertThrows(DatesInconsistencyException.class, () -> {
            bookingService.validateDates(start, start);
        });
    }

    @Test
    void validateDatesPositive() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        assertDoesNotThrow(() -> {
            bookingService.validateDates(start, end);
        });
    }

    @Test
    void testGetUserBookings() {
        assertThrows(EntityNotFoundException.class, () -> bookingService.getUserBookings("ALL", 1L, 0, 100));

        when(userRepositoryMock.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> bookingService.getUserBookings("ALL", 1L, 0, 100));
        assertDoesNotThrow(() -> bookingService.getUserBookings("CURRENT", 1L, 0, 100));
        assertDoesNotThrow(() -> bookingService.getUserBookings("PAST", 1L, 0, 100));
        assertDoesNotThrow(() -> bookingService.getUserBookings("FUTURE", 1L, 0, 100));
        assertDoesNotThrow(() -> bookingService.getUserBookings("WAITING", 1L, 0, 100));
        assertDoesNotThrow(() -> bookingService.getUserBookings("REJECTED", 1L, 0, 100));

        assertThrows(UnsupportedOperationException.class, () -> bookingService.getUserBookings("JOPA", 1L, 0, 100));
    }

    @Test
    void testGetOwnerBookings() {
        assertThrows(EntityNotFoundException.class, () -> bookingService.getOwnerBookings("ALL", 1L, 0, 100));

        when(userRepositoryMock.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> bookingService.getOwnerBookings("ALL", 1L, 0, 100));
        assertDoesNotThrow(() -> bookingService.getOwnerBookings("CURRENT", 1L, 0, 100));
        assertDoesNotThrow(() -> bookingService.getOwnerBookings("PAST", 1L, 0, 100));
        assertDoesNotThrow(() -> bookingService.getOwnerBookings("FUTURE", 1L, 0, 100));
        assertDoesNotThrow(() -> bookingService.getOwnerBookings("WAITING", 1L, 0, 100));
        assertDoesNotThrow(() -> bookingService.getOwnerBookings("REJECTED", 1L, 0, 100));

        assertThrows(UnsupportedOperationException.class, () -> bookingService.getOwnerBookings("JOPA", 1L, 0, 100));
    }
}