package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.booking.dto.BookingLiteDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;
import java.util.Optional;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class FullItemDto extends ItemDto {
    private BookingLiteDto lastBooking;
    private BookingLiteDto nextBooking;
    private List<CommentDto> comments;

    public FullItemDto(Item item, BookingLiteDto lastBooking, BookingLiteDto nextBooking, List<CommentDto> comments) {
        super(item.getId(), item.getName(), item.getDescription(), item.isAvailable(), item.getOwner().getId(),
                Optional.ofNullable(item.getRequest()).map(ItemRequest::getId).orElse(null));
        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;
        this.comments = comments;
    }
}
