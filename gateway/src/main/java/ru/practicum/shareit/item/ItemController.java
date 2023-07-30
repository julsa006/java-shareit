package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
@Validated
public class ItemController {
    private final ItemClient client;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody CreateItemDto request, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return client.createItem(userId, request);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@Valid @RequestBody UpdateItemDto request, @PathVariable Long itemId,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        return client.updateItem(userId, itemId, request);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return client.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                          @RequestParam(defaultValue = "10") @Positive int size) {
        return client.getUserItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text,
                             @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                             @RequestParam(defaultValue = "10") @Positive int size) {
        return client.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CreateCommentDto request, @PathVariable Long itemId,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        return client.createComment(userId, itemId, request);
    }
}
