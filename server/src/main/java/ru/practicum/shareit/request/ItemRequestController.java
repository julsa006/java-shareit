package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(@RequestBody CreateItemRequestDto request,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ItemRequestMapper.toItemRequestDto(itemRequestService.create(request.getDescription(), userId));
    }

    @GetMapping
    public List<ItemRequestWithItemsDto> getOwn(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getOwn(userId).stream().map(ItemRequestMapper::toItemRequestWithItemsDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithItemsDto get(@PathVariable Long requestId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ItemRequestMapper.toItemRequestWithItemsDto(itemRequestService.get(requestId, userId));
    }

    @GetMapping("/all")
    public List<ItemRequestWithItemsDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                @RequestParam(defaultValue = "10") @Positive int size) {
        return itemRequestService.getAll(from, size, userId).stream().map(ItemRequestMapper::toItemRequestWithItemsDto)
                .collect(Collectors.toList());
    }

}
