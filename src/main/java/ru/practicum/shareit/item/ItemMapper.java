package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.FullItemDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getId()
        );
    }

    public static FullItemDto toOwnersItemDto(FullItem item) {
        List<CommentDto> comments = null;
        if (item.getComments() != null) {
            comments = item.getComments().stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
        }
        return new FullItemDto(
                item.getItem(),
                BookingMapper.toBookingLiteDto(item.getLastBooking()),
                BookingMapper.toBookingLiteDto(item.getNextBooking()),
                comments
        );
    }
}
