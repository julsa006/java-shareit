package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemForRequestsDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemRequestWithItemsDto {
    Long id;
    String description;
    LocalDateTime created;
    List<ItemForRequestsDto> items;
}
