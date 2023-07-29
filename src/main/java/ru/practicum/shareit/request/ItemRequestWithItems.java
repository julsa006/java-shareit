package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.Item;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemRequestWithItems {
    private ItemRequest itemRequest;
    private List<Item> items;
}
