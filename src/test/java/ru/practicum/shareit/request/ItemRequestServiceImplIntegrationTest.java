package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.FullItem;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ItemRequestServiceImplIntegrationTest {
    @Autowired
    ItemRequestService itemRequestService;

    @Autowired
    ItemService itemService;

    @Autowired
    UserService userService;

    User owner;
    User requestor;

    @BeforeEach
    void setup() {
        owner = userService.create("owner", "owner@example.com");
        requestor = userService.create("requestor", "requestor@example.com");
    }

    @Test
    void createItemRequest() {
        assertThrows(EntityNotFoundException.class, () -> itemRequestService.create("desc", 100500L));

        ItemRequest itemRequest = itemRequestService.create("desc", requestor.getId());
        assertEquals("desc", itemRequest.getDescription());
        assertEquals(requestor, itemRequest.getRequestor());
        assertNotNull(itemRequest.getId());
        assertNotNull(itemRequest.getCreated());

        ItemRequestWithItems itemRequestGet = itemRequestService.get(itemRequest.getId(), requestor.getId());
        assertEquals(0, itemRequestGet.getItems().size());
        assertEquals(itemRequest, itemRequestGet.getItemRequest());
    }

    @Test
    void createItemForItemRequest() {
        ItemRequest itemRequest = itemRequestService.create("desc", requestor.getId());
        Item item = itemService.create("name", "desc", true, itemRequest.getId(), owner.getId());
        assertEquals(itemRequest, item.getRequest());
        FullItem itemGet = itemService.get(item.getId(), owner.getId());
        assertEquals(item, itemGet.getItem());
    }
}
