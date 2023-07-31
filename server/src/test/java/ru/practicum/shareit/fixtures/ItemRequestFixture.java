package ru.practicum.shareit.fixtures;

import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

public class ItemRequestFixture {
    private static Long idSerial = 1L;

    public static ItemRequest getItemRequest(User requestor) {
        return getItemRequest(idSerial++, requestor);
    }

    public static ItemRequest getItemRequest(Long id, User requestor) {
        return new ItemRequest(
                id,
                String.format("Item request desctrpiton %d", id),
                requestor,
                LocalDateTime.now()
        );
    }
}
