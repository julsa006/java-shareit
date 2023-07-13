package ru.practicum.shareit.item;

import java.util.List;

public interface ItemDao {
    Item create(String name, String description, boolean available, Long ownerId);

    Item get(Long id);

    List<Item> getUserItems(Long userId);

    List<Item> searchItems(String text);

    Item update(Long id, String name, String description, Boolean available);
}
