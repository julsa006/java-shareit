package ru.practicum.shareit.fixtures;

import ru.practicum.shareit.user.User;

public class UserFixture {
    private static Long idSequence = 1L;

    public static User getUser() {
        return getUser(idSequence++);
    }

    public static User getUser(Long id) {
        return new User(id, "name", String.format("name-%d@example.com", System.currentTimeMillis()));
    }

    public static User getUser(Long id, String email) {
        return new User(id, "name", email);
    }
}
