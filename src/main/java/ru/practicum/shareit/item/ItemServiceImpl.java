package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.CommentWithoutBookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
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

    @Override
    public Item create(String name, String description, boolean available, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException(String.format("User %s not found", ownerId)));
        return itemRepository.save(new Item(null, name, description, available, owner));
    }

    @Override
    public Item update(Long id, String name, String description, Boolean available, Long updaterId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Item %s not found", id)));
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
                .orElseThrow(() -> new NotFoundException(String.format("Item %d not found", id)));
        FullItem ownersItem = new FullItem(item, null, null, commentRepository.findByItemId(id));
        if (item.getOwner().getId().equals(userId)) {
            ownersItem.setLastBooking(bookingRepository.findLastBooking(id));
            ownersItem.setNextBooking(bookingRepository.findNextBooking(id));
        }
        return ownersItem;
    }

    @Override
    public List<FullItem> getUserItems(Long userId) {
        List<FullItem> items = itemRepository.findAllByOwnerIdOrderById(userId).stream()
                .map(item -> new FullItem(item,
                        bookingRepository.findLastBooking(item.getId()),
                        bookingRepository.findNextBooking(item.getId()), null))
                .collect(Collectors.toList());
        Map<Long, FullItem> itemsMap = new HashMap<>();
        for (FullItem item : items) {
            itemsMap.put(item.getItem().getId(), item);
        }
        List<Comment> comments = commentRepository.findByItemIdIn(items.stream().map(i -> i.getItem().getId())
                .collect(Collectors.toList()));
        for (Comment comment : comments) {
            itemsMap.get(comment.getAuthor().getId()).getComments().add(comment);
        }
        return items;
    }

    @Override
    public List<Item> searchItems(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.findAllByText(text);
    }

    @Override
    public Comment createComment(String text, Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item %s not found", itemId)));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User %s not found", userId)));
        if (bookingRepository.countItemUserBookings(itemId, userId) == 0) {
            throw new CommentWithoutBookingException(String.format("User %d cannot comment item %d", userId, itemId));
        }
        return commentRepository.save(new Comment(null, text, item, user, LocalDateTime.now()));
    }
}
