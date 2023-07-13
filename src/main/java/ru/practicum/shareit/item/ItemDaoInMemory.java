package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ItemDaoInMemory implements ItemDao {
    private final UserDao userDao;
    private final Map<Long, Item> items = new HashMap<>();
    private long curId = 0;

    @Override
    public Item create(String name, String description, boolean available, Long ownerId) {
        User owner = userDao.get(ownerId);
        if (owner == null) {
            throw new NotFoundException(String.format("User %d not found", ownerId));
        }
        Item item = new Item(++curId, name, description, available, owner);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item get(Long id) {
        return items.get(id);
    }

    @Override
    public List<Item> getUserItems(Long userId) {
        List<Item> userItems = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner().getId().equals(userId)) {
                userItems.add(item);
            }
        }
        return userItems;
    }

    @Override
    public List<Item> searchItems(String text) {
        List<Item> searchItems = new ArrayList<>();
        for (Item item : items.values()) {
            if (!item.isAvailable()) {
                continue;
            }
            if (item.getName().toLowerCase().contains(text.toLowerCase())
                    || item.getDescription().toLowerCase().contains(text.toLowerCase())) {
                searchItems.add(item);
            }
        }
        return searchItems;
    }

    @Override
    public Item update(Long id, String name, String description, Boolean available) {
        if (!items.containsKey(id)) {
            throw new NotFoundException(String.format("Item %d not found", id));
        }
        Item item = items.get(id);
        if (name != null) {
            item.setName(name);
        }
        if (description != null) {
            item.setDescription(description);
        }
        if (available != null) {
            item.setAvailable(available);
        }
        return item;
    }

    public void deleteByOwner(Long ownerId) {
        items.entrySet().removeIf(e -> e.getValue().getOwner().getId().equals(ownerId));
    }
}
