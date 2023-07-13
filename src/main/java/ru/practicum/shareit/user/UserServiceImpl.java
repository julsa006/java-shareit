package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    @Override
    public List<User> getAll() {
        return userDao.findAll();
    }

    @Override
    public User create(String name, String email) {
        return userDao.create(name, email);
    }

    @Override
    public User update(Long id, String name, String email) {
        return userDao.update(id, name, email);
    }


    @Override
    public User get(Long id) {
        User user = userDao.get(id);
        if (user == null) {
            throw new NotFoundException(String.format("User %s not found", id));
        }
        return user;
    }

    @Override
    public void delete(Long id) {
        userDao.delete(id);
    }
}
