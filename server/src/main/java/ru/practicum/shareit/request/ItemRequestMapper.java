package ru.practicum.shareit.request;

import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemForRequestsDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;

import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated()
        );
    }

    public static ItemRequestWithItemsDto toItemRequestWithItemsDto(ItemRequestWithItems itemRequestWithItems) {
        ItemRequest itemRequest = itemRequestWithItems.getItemRequest();
        List<ItemForRequestsDto> items = itemRequestWithItems.getItems().stream().map(ItemMapper::toItemForRequestsDto)
                .collect(Collectors.toList());
        return new ItemRequestWithItemsDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                items
        );
    }
}
