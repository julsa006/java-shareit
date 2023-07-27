package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    Item create(String name, String description, boolean available, Long ownerId);

    Item update(Long id, String name, String description, Boolean available, Long updaterId);

    FullItem get(Long id, Long userId);

    List<FullItem> getUserItems(Long userId);

    List<Item> searchItems(String text);

    Comment createComment(String text, Long itemId, Long userId);
}
