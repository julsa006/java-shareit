package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequest create(String description, Long userId) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User %d not found", userId)));
        return itemRequestRepository.save(new ItemRequest(null, description, requestor, LocalDateTime.now()));
    }

    @Override
    public List<ItemRequestWithItems> getOwn(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(String.format("User %d not found", userId));
        }

        List<ItemRequest> requests = itemRequestRepository
                .findAllByRequestorIdOrderByCreatedDesc(userId);

        return getItemRequestWithItems(requests);
    }

    protected List<ItemRequestWithItems> getItemRequestWithItems(List<ItemRequest> requests) {
        List<Item> items = itemRepository.findAllByRequestIdIn(requests.stream()
                .map(ItemRequest::getId).collect(Collectors.toList()));

        Map<Long, List<Item>> itemsMap = items.stream().collect(Collectors.groupingBy(
                i -> i.getRequest().getId(),
                Collectors.toList()
        ));

        return requests.stream().map(r -> new ItemRequestWithItems(r, itemsMap.getOrDefault(r.getId(), List.of()))).collect(Collectors.toList());
    }

    @Override
    public ItemRequestWithItems get(Long requestId, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(String.format("User %d not found", userId));
        }
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Request %d not found", requestId)));
        List<Item> items = itemRepository.findAllByRequestId(requestId);
        return new ItemRequestWithItems(itemRequest, items);
    }

    @Override
    public List<ItemRequestWithItems> getAll(int from, int size, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(String.format("User %d not found", userId));
        }
        PageRequest page = PageRequest.of(from / size, size);
        return getItemRequestWithItems(itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(userId, page).getContent());
    }
}
