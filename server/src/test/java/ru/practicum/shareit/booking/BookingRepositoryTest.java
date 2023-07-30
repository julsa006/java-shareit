package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.fixtures.BookingFixture;
import ru.practicum.shareit.fixtures.ItemFixture;
import ru.practicum.shareit.fixtures.UserFixture;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookingRepositoryTest {
    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BookingRepository bookingRepository;

    User user;
    User booker;
    Item item1;
    Item item2;
    Item item3;

    @BeforeEach
    void setup() {
        user = userRepository.save(UserFixture.getUser(null));
        booker = userRepository.save(UserFixture.getUser(null));
        item1 = itemRepository.save(ItemFixture.getItem(null, user));
        item2 = itemRepository.save(ItemFixture.getItem(null, user));
        item3 = itemRepository.save(ItemFixture.getItem(null, user));
    }


    @Test
    void findLastBooking() {
        Booking pastPastBooking1 = bookingRepository.save(BookingFixture.getPastBooking(null, booker, item1));
        Booking pastBooking1 = bookingRepository.save(BookingFixture.getActualBooking(null, booker, item1));
        Booking pastBooking2 = bookingRepository.save(BookingFixture.getPastBooking(null, booker, item2));
        Booking futureBooking1 = bookingRepository.save(BookingFixture.getFutureBooking(null, booker, item1));
        Booking futureBooking3 = bookingRepository.save(BookingFixture.getFutureBooking(null, booker, item3));

        assertEquals(pastBooking1, bookingRepository.findLastBooking(item1.getId()));
        assertEquals(pastBooking2, bookingRepository.findLastBooking(item2.getId()));
        assertNull(bookingRepository.findLastBooking(item3.getId()));

        List<Booking> result = bookingRepository.findLastBookings(List.of(item1.getId(), item2.getId(), item3.getId()));
        result.sort((a, b) -> (int) (a.getId() - b.getId()));
        List<Booking> expected = List.of(pastBooking1, pastBooking2);
        assertEquals(expected, result);
    }

    @Test
    void findNextBookings() {
        Booking pastBooking1 = bookingRepository.save(BookingFixture.getActualBooking(null, booker, item1));
        Booking futureBooking1 = bookingRepository.save(BookingFixture.getFutureBooking(null, booker, item1));
        Booking farFutureBooking1 = BookingFixture.getActualBooking(null, booker, item1);
        farFutureBooking1.setStartTime(farFutureBooking1.getStartTime().plusDays(3));
        farFutureBooking1.setEndTime(farFutureBooking1.getEndTime().plusDays(3));
        farFutureBooking1 = bookingRepository.save(farFutureBooking1);

        Booking futureBooking2 = bookingRepository.save(BookingFixture.getFutureBooking(null, booker, item2));

        Booking pastBooking3 = bookingRepository.save(BookingFixture.getPastBooking(null, booker, item3));

        assertEquals(futureBooking1, bookingRepository.findNextBooking(item1.getId()));
        assertEquals(futureBooking2, bookingRepository.findNextBooking(item2.getId()));
        assertNull(bookingRepository.findNextBooking(item3.getId()));

        List<Booking> result = bookingRepository.findNextBookings(List.of(item1.getId(), item2.getId(), item3.getId()));
        result.sort((a, b) -> (int) (a.getId() - b.getId()));
        List<Booking> expected = List.of(futureBooking1, futureBooking2);
        assertEquals(expected, result);
    }

    @Test
    void countItemUserBookings() {
        assertEquals(0, bookingRepository.countItemUserBookings(item1.getId(), booker.getId()));

        bookingRepository.save(BookingFixture.getFutureBooking(null, booker, item1));
        assertEquals(0, bookingRepository.countItemUserBookings(item1.getId(), booker.getId()));

        bookingRepository.save(BookingFixture.getActualBooking(null, booker, item1));
        assertEquals(0, bookingRepository.countItemUserBookings(item1.getId(), booker.getId()));

        bookingRepository.save(BookingFixture.getPastBooking(null, booker, item1));
        assertEquals(1, bookingRepository.countItemUserBookings(item1.getId(), booker.getId()));
    }
}