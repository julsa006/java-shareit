package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService service;

    @PostMapping
    public ItemDto create(@RequestBody CreateItemDto item, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ItemMapper.toItemDto(service.create(item.getName(), item.getDescription(), item.getAvailable(), item.getRequestId(), userId));
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody UpdateItemDto item, @PathVariable Long itemId,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ItemMapper.toItemDto(service.update(itemId, item.getName(), item.getDescription(),
                item.getAvailable(), userId));
    }

    @GetMapping("/{itemId}")
    public FullItemDto get(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ItemMapper.toOwnersItemDto(service.get(itemId, userId));
    }

    @GetMapping
    public List<FullItemDto> getUserItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestParam(defaultValue = "0") int from,
                                          @RequestParam(defaultValue = "10") int size) {
        return service.getUserItems(userId, from, size).stream().map(ItemMapper::toOwnersItemDto).collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> get(@RequestParam String text,
                             @RequestParam(defaultValue = "0") int from,
                             @RequestParam(defaultValue = "10") int size) {
        return service.searchItems(text, from, size).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody CreateCommentDto comment, @PathVariable Long itemId,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        return CommentMapper.toCommentDto(service.createComment(comment.getText(), itemId, userId));
    }
}
