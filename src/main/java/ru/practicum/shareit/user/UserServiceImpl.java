package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<User> getAll() {
        return repository.findAll();
    }

    @Override
    public User create(String name, String email) {
        return repository.save(new User(null, name, email));
    }

    @Override
    public User update(Long id, String name, String email) {
        User user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User %d not found", id)));
        if (name != null) {
            user.setName(name);
        }
        if (email != null) {
            user.setEmail(email);
        }
        return repository.save(user);
    }


    @Override
    public User get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User %d not found", id)));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
