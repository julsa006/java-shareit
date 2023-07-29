package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    Item create(String name, String description, boolean available, Long requestId, Long ownerId);

    Item update(Long id, String name, String description, Boolean available, Long updaterId);

    FullItem get(Long id, Long userId);

    List<FullItem> getUserItems(Long userId, int from, int size);

    List<Item> searchItems(String text, int from, int size);

    Comment createComment(String text, Long itemId, Long userId);
}
