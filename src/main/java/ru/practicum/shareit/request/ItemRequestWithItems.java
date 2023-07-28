package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.Item;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemRequestWithItems {
    ItemRequest itemRequest;
    List<Item> items;
}
