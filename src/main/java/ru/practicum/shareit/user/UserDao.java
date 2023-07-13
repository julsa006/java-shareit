package ru.practicum.shareit.user;

import java.util.List;

public interface UserDao {
    List<User> findAll();

    User get(Long id);

    void delete(Long id);

    User create(String name, String email);

    User update(Long id, String name, String email);
}
