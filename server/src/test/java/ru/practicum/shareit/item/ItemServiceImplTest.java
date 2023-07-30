package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.fixtures.BookingFixture;
import ru.practicum.shareit.fixtures.CommentFixture;
import ru.practicum.shareit.fixtures.ItemFixture;
import ru.practicum.shareit.fixtures.UserFixture;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.utils.DBMockTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@SpringJUnitConfig({ItemServiceImpl.class})
class ItemServiceImplTest extends DBMockTest {
    @Autowired
    ItemService itemService;

    @Test
    void testGetUserItems() {
        User user = UserFixture.getUser();
        User booker1 = UserFixture.getUser();
        User booker2 = UserFixture.getUser();

        Item item1 = ItemFixture.getItem(user);
        Booking booking1 = BookingFixture.getActualBooking(booker1, item1);
        Booking booking2 = BookingFixture.getActualBooking(booker2, item1);
        booking2.setStartTime(LocalDateTime.now().plusDays(3));
        booking2.setEndTime(LocalDateTime.now().plusDays(4));

        Item item2 = ItemFixture.getItem(user);
        Booking booking3 = BookingFixture.getActualBooking(booker2, item2);
        booking3.setStartTime(LocalDateTime.now().minusDays(10));
        booking3.setEndTime(LocalDateTime.now().minusDays(9));
        Comment item2Comment1 = CommentFixture.getComment(item2, booker2);

        Item item3 = ItemFixture.getItem(user);

        when(itemRepositoryMock.findAllByOwnerIdOrderById(eq(user.getId()), any())).thenReturn(
                List.of(item1, item2, item3)
        );

        List<Long> allItemsIds = List.of(item1.getId(), item2.getId(), item3.getId());
        when(bookingRepositoryMock.findLastBookings(allItemsIds)).thenReturn(
                List.of(booking1, booking3)
        );
        when(bookingRepositoryMock.findNextBookings(allItemsIds)).thenReturn(
                List.of(booking2)
        );
        when(commentRepositoryMock.findByItemIdIn(allItemsIds)).thenReturn(
                List.of(item2Comment1)
        );

        List<FullItem> expected = List.of(
                new FullItem(item1, booking1, booking2, List.of()),
                new FullItem(item2, booking3, null, List.of(item2Comment1)),
                new FullItem(item3, null, null, List.of())
        );

        List<FullItem> actual = itemService.getUserItems(user.getId(), 0, 100);
        assertEquals(expected, actual);
    }

}