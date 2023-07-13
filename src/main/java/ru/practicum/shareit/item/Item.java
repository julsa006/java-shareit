package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.User;

@Data
@AllArgsConstructor
public class Item {
    Long id;
    String name;
    String description;
    boolean available;
    User owner;
}
