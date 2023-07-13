package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    List<User> getAll();

    User create(String name, String email);

    User update(Long id, String name, String email);

    User get(Long id);

    void delete(Long id);
}
