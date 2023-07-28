package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.CommentWithoutBookingException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public Item create(String name, String description, boolean available, Long requestId, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User %s not found", ownerId)));
        ItemRequest request = null;
        if (requestId != null) {
            request = itemRequestRepository.findById(requestId)
                    .orElseThrow(() -> new EntityNotFoundException(String.format("Request %s not found", requestId)));
        }
        return itemRepository.save(new Item(null, name, description, available, owner, request));
    }

    @Override
    public Item update(Long id, String name, String description, Boolean available, Long updaterId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Item %s not found", id)));
        if (!item.getOwner().getId().equals(updaterId)) {
            throw new AccessDeniedException(String.format("User %d has no item %d", updaterId, id));
        }
        if (name != null) {
            item.setName(name);
        }
        if (description != null) {
            item.setDescription(description);
        }
        if (available != null) {
            item.setAvailable(available);
        }
        return itemRepository.save(item);
    }

    @Override
    public FullItem get(Long id, Long userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Item %d not found", id)));
        FullItem ownersItem = new FullItem(item, null, null, commentRepository.findByItemId(id));
        if (item.getOwner().getId().equals(userId)) {
            ownersItem.setLastBooking(bookingRepository.findLastBooking(id));
            ownersItem.setNextBooking(bookingRepository.findNextBooking(id));
        }
        return ownersItem;
    }

    @Override
    public List<FullItem> getUserItems(Long userId, int from, int size) {
        PageRequest page = PageRequest.of(from / size, size);
        List<Item> items = itemRepository.findAllByOwnerIdOrderById(userId, page);
        Map<Long, FullItem> itemsMap = new LinkedHashMap<>();
        for (Item item : items) {
            itemsMap.put(item.getId(), new FullItem(item, null, null, new ArrayList<>()));
        }
        List<Long> itemIds = items.stream().map(Item::getId)
                .collect(Collectors.toList());

        List<Booking> lastBookings = bookingRepository.findLastBookings(itemIds);
        for (Booking booking : lastBookings) {
            itemsMap.get(booking.getItem().getId()).setLastBooking(booking);
        }

        List<Booking> nextBookings = bookingRepository.findNextBookings(itemIds);
        for (Booking booking : nextBookings) {
            itemsMap.get(booking.getItem().getId()).setNextBooking(booking);
        }

        List<Comment> comments = commentRepository.findByItemIdIn(itemIds);
        for (Comment comment : comments) {
            itemsMap.get(comment.getItem().getId()).getComments().add(comment);
        }
        return new ArrayList<>(itemsMap.values());
    }

    @Override
    public List<Item> searchItems(String text, int from, int size) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        PageRequest page = PageRequest.of(from / size, size);
        return itemRepository.findAllByTextOrderById(text, page);
    }

    @Override
    public Comment createComment(String text, Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Item %s not found", itemId)));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User %s not found", userId)));
        if (bookingRepository.countItemUserBookings(itemId, userId) == 0) {
            throw new CommentWithoutBookingException(String.format("User %d cannot comment item %d", userId, itemId));
        }
        return commentRepository.save(new Comment(null, text, item, user, LocalDateTime.now()));
    }
}
