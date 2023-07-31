package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.CommentWithoutBookingException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.fixtures.BookingFixture;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ItemServiceImplIntegrationTest {
    @Autowired
    ItemService itemService;

    @Autowired
    UserService userService;

    @Autowired
    BookingRepository bookingRepository;

    User user;

    @BeforeEach
    void setup() {
        user = userService.create("name", "name@example.com");
    }

    @Test
    void createItem() {
        assertThrows(EntityNotFoundException.class, () -> itemService.create("name", "description", true, null, 100500L));

        Item item = itemService.create("name", "description", true, null, user.getId());
        assertEquals("name", item.getName());
        assertEquals("description", item.getDescription());
        assertEquals(user, item.getOwner());
        assertNull(item.getRequest());
        assertTrue(item.isAvailable());

        FullItem itemGet = itemService.get(item.getId(), user.getId());
        assertEquals(new FullItem(item, null, null, List.of()), itemGet);
    }

    @Test
    void updateItem() {
        Item item = itemService.create("name", "description", true, null, user.getId());

        Item updatedItem = itemService.update(item.getId(), "new name", null, null, user.getId());
        assertEquals("new name", updatedItem.getName());
        assertEquals(item.getOwner(), updatedItem.getOwner());
        assertEquals(item.getDescription(), updatedItem.getDescription());
        assertEquals(item.isAvailable(), updatedItem.isAvailable());
        assertEquals(item.getRequest(), updatedItem.getRequest());
    }

    @Test
    void updateItemAccessDenies() {
        Item item = itemService.create("name", "description", true, null, user.getId());
        User otherUser = userService.create("name", "other@example.com");
        assertThrows(AccessDeniedException.class, () -> itemService.update(item.getId(), "new name", null, null, otherUser.getId()));
    }

    @Test
    void getUserItem() {
        List<Item> items = new ArrayList<>(11);
        for (int i = 0; i < 11; i++) {
            items.add(itemService.create(String.valueOf(i), "description", true, null, user.getId()));
        }

        List<FullItem> getItems = itemService.getUserItems(user.getId(), 0, 100);
        assertEquals(11, getItems.size());

        for (int num = 0; num < 10; num += 2) {
            getItems = itemService.getUserItems(user.getId(), num, 2);
            assertEquals(2, getItems.size());
            assertEquals(String.valueOf(num), getItems.get(0).getItem().getName());
            assertEquals(String.valueOf(num + 1), getItems.get(1).getItem().getName());
        }
        getItems = itemService.getUserItems(user.getId(), 10, 2);
        assertEquals(1, getItems.size());
        assertEquals(String.valueOf(10), getItems.get(0).getItem().getName());
    }

    @Test
    void searchItem() {
        User otherUser = userService.create("other name", "other-email@example.com");

        Item item1 = itemService.create("name to SeArCh", "desc", true, null, user.getId());
        Item item2 = itemService.create("name", "desc to seaRCH", true, null, otherUser.getId());
        Item item3 = itemService.create("name", "desc", true, null, user.getId());

        List<Item> actual = itemService.searchItems("search", 0, 100);
        List<Item> expected = List.of(item1, item2);

        assertEquals(expected, actual);
    }

    @Test
    void createComment() {
        User otherUser = userService.create("other name", "other-email@example.com");
        Item item = itemService.create("name", "desc", true, null, user.getId());

        assertThrows(EntityNotFoundException.class, () -> itemService.createComment("test comment", 100500L, otherUser.getId()));
        assertThrows(EntityNotFoundException.class, () -> itemService.createComment("test comment", item.getId(), 100500L));
        assertThrows(CommentWithoutBookingException.class, () -> itemService.createComment("test comment", item.getId(), otherUser.getId()));

        bookingRepository.save(BookingFixture.getPastBooking(item.getId(), otherUser, item));
        itemService.createComment("test comment", item.getId(), otherUser.getId());
    }
}
