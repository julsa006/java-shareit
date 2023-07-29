package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.UnavailableItemException;
import ru.practicum.shareit.exception.UnsupportedOperationException;
import ru.practicum.shareit.item.FullItem;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BookingServiceImplIntegrationTest {
    @Autowired
    BookingService bookingService;

    @Autowired
    ItemService itemService;

    @Autowired
    UserService userService;

    User owner;
    Item item;
    User booker;
    LocalDateTime now;

    @BeforeEach
    void setup() {
        owner = userService.create("owner", "owner@example.com");
        item = itemService.create("name", "description", true, null, owner.getId());
        booker = userService.create("booker", "booker@example.com");
        now = LocalDateTime.now();
    }

    @Test
    void createBooking() {
        assertThrows(EntityNotFoundException.class, () -> bookingService.create(100500L, now.plusDays(1), now.plusDays(2), owner.getId()));
        assertThrows(EntityNotFoundException.class, () -> bookingService.create(item.getId(), now.plusDays(1), now.plusDays(2), 100500L));
        assertThrows(EntityNotFoundException.class, () -> bookingService.create(item.getId(), now.plusDays(1), now.plusDays(2), owner.getId()));

        Booking booking = bookingService.create(item.getId(), now.plusDays(1), now.plusDays(2), booker.getId());
        assertEquals(item, booking.getItem());
        assertEquals(booker, booking.getBooker());
        assertEquals(now.plusDays(1), booking.getStartTime());
        assertEquals(now.plusDays(2), booking.getEndTime());
        assertEquals(BookingStatus.WAITING, booking.getStatus());
        assertNotNull(booking.getId());

        FullItem itemGet = itemService.get(item.getId(), owner.getId());
        assertEquals(new FullItem(item, null, null, List.of()), itemGet);
    }

    @Test
    void createBookingOnUnavailableItem() {
        Item unavailableItem = itemService.create("name", "desc", false, null, owner.getId());
        assertThrows(UnavailableItemException.class, () -> bookingService.create(unavailableItem.getId(), now.plusDays(1), now.plusDays(2), booker.getId()));
    }

    @Test
    void approve() {
        LocalDateTime start = now.plusDays(1);
        LocalDateTime end = now.plusDays(2);
        Booking booking = bookingService.create(item.getId(), start, end, booker.getId());
        Long bookingId = booking.getId();
        assertEquals(BookingStatus.WAITING, booking.getStatus());

        assertThrows(EntityNotFoundException.class, () -> bookingService.approve(100500L, true, booker.getId()));
        assertThrows(EntityNotFoundException.class, () -> bookingService.approve(bookingId, true, booker.getId()));

        booking = bookingService.approve(bookingId, true, owner.getId());
        assertEquals(BookingStatus.APPROVED, booking.getStatus());
        booking = bookingService.get(bookingId, owner.getId());
        assertEquals(BookingStatus.APPROVED, booking.getStatus());

        assertThrows(UnsupportedOperationException.class, () -> bookingService.approve(bookingId, true, owner.getId()));
    }

    @Test
    void reject() {
        LocalDateTime start = now.plusDays(1);
        LocalDateTime end = now.plusDays(2);
        Booking booking = bookingService.create(item.getId(), start, end, booker.getId());
        Long bookingId = booking.getId();
        assertEquals(BookingStatus.WAITING, booking.getStatus());

        booking = bookingService.approve(bookingId, false, owner.getId());
        assertEquals(BookingStatus.REJECTED, booking.getStatus());
        booking = bookingService.get(bookingId, owner.getId());
        assertEquals(BookingStatus.REJECTED, booking.getStatus());
    }
}
