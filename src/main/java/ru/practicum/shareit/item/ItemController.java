package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService service;

    @PostMapping
    public ItemDto create(@Valid @RequestBody CreateItemDto item, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ItemMapper.toItemDto(service.create(item.getName(), item.getDescription(), item.getAvailable(), userId));
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@Valid @RequestBody UpdateItemDto item, @PathVariable Long itemId,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ItemMapper.toItemDto(service.update(itemId, item.getName(), item.getDescription(),
                item.getAvailable(), userId));
    }

    @GetMapping("/{itemId}")
    public FullItemDto get(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ItemMapper.toOwnersItemDto(service.get(itemId, userId));
    }

    @GetMapping
    public List<FullItemDto> getUserItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getUserItems(userId).stream().map(ItemMapper::toOwnersItemDto).collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> get(@RequestParam String text) {
        return service.searchItems(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@Valid @RequestBody CreateCommentDto comment, @PathVariable Long itemId,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        return CommentMapper.toCommentDto(service.createComment(comment.getText(), itemId, userId));
    }
}
