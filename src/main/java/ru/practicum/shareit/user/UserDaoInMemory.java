package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotUniqueEmailException;
import ru.practicum.shareit.item.ItemDaoInMemory;

import java.util.*;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Lazy))
public class UserDaoInMemory implements UserDao {
    @Lazy
    private final ItemDaoInMemory itemDao;

    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private Long curId = 0L;

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(String name, String email) {
        if (emails.contains(email)) {
            throw new NotUniqueEmailException(email);
        }
        User user = new User(++curId, name, email);
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }

    @Override
    public User update(Long id, String name, String email) {
        if (!users.containsKey(id)) {
            throw new NotFoundException(String.format("User %s not found", id));
        }
        User storageUser = users.get(id);
        if (email != null && emails.contains(email) && !email.equals(storageUser.getEmail())) {
            throw new NotUniqueEmailException(email);
        }

        if (name != null) {
            storageUser.setName(name);
        }
        if (email != null) {
            emails.remove(storageUser.getEmail());
            storageUser.setEmail(email);
            emails.add(storageUser.getEmail());
        }
        return storageUser;
    }

    @Override
    public User get(Long id) {
        return users.get(id);
    }

    @Override
    public void delete(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException(String.format("User %s not found", id));
        }
        User user = users.remove(id);
        emails.remove(user.getEmail());
        itemDao.deleteByOwner(id);
    }
}
