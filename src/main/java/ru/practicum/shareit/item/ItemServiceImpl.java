package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemDao;

    @Override
    public Item create(String name, String description, boolean available, Long ownerId) {
        return itemDao.create(name, description, available, ownerId);
    }

    @Override
    public Item update(Long id, String name, String description, Boolean available, Long updaterId) {
        Item item = itemDao.get(id);
        if (item == null) {
            throw new NotFoundException(String.format("Item %d not found", id));
        }
        if (!item.getOwner().getId().equals(updaterId)) {
            throw new AccessDeniedException(String.format("User %d has no item %d", updaterId, id));
        }
        return itemDao.update(id, name, description, available);
    }

    @Override
    public Item get(Long id) {
        return itemDao.get(id);
    }

    @Override
    public List<Item> getUserItems(Long userId) {
        return itemDao.getUserItems(userId);
    }

    @Override
    public List<Item> searchItems(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemDao.searchItems(text);
    }
}
