package ru.practicum.shareit.request;

import java.util.List;

public interface ItemRequestService {
    ItemRequest create(String description, Long userId);

    List<ItemRequestWithItems> getOwn(Long userId);

    ItemRequestWithItems get(Long requestId, Long userId);

    List<ItemRequestWithItems> getAll(int from, int size, Long userId);
}
