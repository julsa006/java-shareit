package ru.practicum.shareit.fixtures;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

public class ItemFixture {
    private static Long idSequence = 1L;

    public static Item getItem(User user) {
        return getItem(idSequence++, user);
    }

    public static Item getItem(Long id, User user) {
        return new Item(
                id,
                String.format("Item-%d", id),
                String.format("Item-%d test description", id),
                true,
                user,
                null
        );
    }
}
