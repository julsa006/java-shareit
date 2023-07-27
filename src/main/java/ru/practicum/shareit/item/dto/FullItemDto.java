package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingLiteDto;
import ru.practicum.shareit.item.Item;

import java.util.List;

@Data
@AllArgsConstructor
public class FullItemDto extends ItemDto {
    private BookingLiteDto lastBooking;
    private BookingLiteDto nextBooking;
    private List<CommentDto> comments;

    public FullItemDto(Item item, BookingLiteDto lastBooking, BookingLiteDto nextBooking, List<CommentDto> comments) {
        super(item.getId(), item.getName(), item.getDescription(), item.isAvailable(), item.getOwner().getId());
        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;
        this.comments = comments;
    }
}
