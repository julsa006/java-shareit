package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.fixtures.ItemFixture;
import ru.practicum.shareit.fixtures.ItemRequestFixture;
import ru.practicum.shareit.fixtures.UserFixture;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.utils.DBMockTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@SpringJUnitConfig({ItemRequestServiceImpl.class})
class ItemRequestServiceImplTest extends DBMockTest {
    @Autowired
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void testGetItemRequsetWithItems() {
        User user = UserFixture.getUser();

        ItemRequest itemRequest1 = ItemRequestFixture.getItemRequest(user);
        Item item1 = ItemFixture.getItem(user);
        item1.setRequest(itemRequest1);

        ItemRequest itemRequest2 = ItemRequestFixture.getItemRequest(user);
        Item item2a = ItemFixture.getItem(user);
        item2a.setRequest(itemRequest2);
        Item item2b = ItemFixture.getItem(user);
        item2b.setRequest(itemRequest2);

        ItemRequest itemRequest3 = ItemRequestFixture.getItemRequest(user);

        when(itemRepositoryMock.findAllByRequestIdIn(any())).thenReturn(List.of(item1, item2a, item2b));


        List<ItemRequestWithItems> actual = itemRequestService.getItemRequestWithItems(
                List.of(itemRequest1, itemRequest2, itemRequest3)
        );
        List<ItemRequestWithItems> expected = List.of(
                new ItemRequestWithItems(itemRequest1, List.of(item1)),
                new ItemRequestWithItems(itemRequest2, List.of(item2a, item2b)),
                new ItemRequestWithItems(itemRequest3, List.of())
        );

        assertEquals(expected, actual);
    }

    @Test
    void testGetAll() {
        assertThrows(EntityNotFoundException.class, () -> itemRequestService.getAll(0, 100, 100500L));

        when(userRepositoryMock.existsById(any())).thenReturn(true);
        when(itemRequestRepositoryMock.findAllByRequestorIdNotOrderByCreatedDesc(any(), any())).thenReturn(Page.empty());
        assertDoesNotThrow(() -> itemRequestService.getAll(0, 100, 1L));
    }

    @Test
    void testOwn() {
        assertThrows(EntityNotFoundException.class, () -> itemRequestService.getOwn(100500L));

        when(userRepositoryMock.existsById(any())).thenReturn(true);
        when(itemRequestRepositoryMock.findAllByRequestorIdOrderByCreatedDesc(any())).thenReturn(List.of());
        assertDoesNotThrow(() -> itemRequestService.getOwn(1L));
    }
}