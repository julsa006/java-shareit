package ru.practicum.shareit.utils;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;

@SpringJUnitConfig
public abstract class DBMockTest {
    @MockBean
    protected ItemRepository itemRepositoryMock;

    @MockBean
    protected BookingRepository bookingRepositoryMock;

    @MockBean
    protected CommentRepository commentRepositoryMock;

    @MockBean
    protected UserRepository userRepositoryMock;

    @MockBean
    protected ItemRequestRepository itemRequestRepositoryMock;
}
