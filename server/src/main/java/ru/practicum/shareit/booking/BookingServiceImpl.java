package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.UnavailableItemException;
import ru.practicum.shareit.exception.UnsupportedOperationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {
    BookingRepository bookingRepository;
    UserRepository userRepository;
    ItemRepository itemRepository;

    @Override
    public Booking create(Long itemId, LocalDateTime start, LocalDateTime end, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User %d not found", userId)));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Item %d not found", itemId)));
        if (userId.equals(item.getOwner().getId())) {
            throw new EntityNotFoundException(String.format("Item %d not found", itemId));
        }
        if (!item.isAvailable()) {
            throw new UnavailableItemException(String.format("Item %d unavailable", item.getId()));
        }
        return bookingRepository.save(new Booking(null, start, end, item,
                user, BookingStatus.WAITING));
    }

    @Override
    public Booking approve(Long bookingId, boolean approved, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Booking %d not found", bookingId)));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new EntityNotFoundException(String.format("Booking %d not found", bookingId));
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new UnsupportedOperationException(String.format("Booking %d status not waiting", bookingId));
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return bookingRepository.save(booking);
    }

    @Override
    public List<Booking> getUserBookings(String stringState, Long userId, int from, int size) {
        State state = getState(stringState);
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(String.format("User %d not found", userId));
        }
        PageRequest page = PageRequest.of(from / size, size);
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                return bookingRepository.findAllByBookerIdOrderByStartTimeDesc(userId, page);
            case CURRENT:
                return bookingRepository.findAllByBookerIdAndStartTimeBeforeAndEndTimeAfterOrderByStartTimeDesc(userId, now, now, page);
            case PAST:
                return bookingRepository.findAllByBookerIdAndEndTimeBeforeOrderByStartTimeDesc(userId, now, page);
            case FUTURE:
                return bookingRepository.findAllByBookerIdAndStartTimeAfterOrderByStartTimeDesc(userId, now, page);
            case WAITING:
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartTimeDesc(userId, BookingStatus.WAITING, page);
            case REJECTED:
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartTimeDesc(userId, BookingStatus.REJECTED, page);
            default:
                throw new UnsupportedOperationException(String.format("Unknown state: %s", state));
        }
    }

    @Override
    public List<Booking> getOwnerBookings(String stringState, Long ownerId, int from, int size) {
        State state = getState(stringState);
        if (!userRepository.existsById(ownerId)) {
            throw new EntityNotFoundException(String.format("User %d not found", ownerId));
        }
        PageRequest page = PageRequest.of(from / size, size);
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                return bookingRepository.findAllByItemOwnerIdOrderByStartTimeDesc(ownerId, page);
            case CURRENT:
                return bookingRepository.findAllByItemOwnerIdAndStartTimeBeforeAndEndTimeAfterOrderByStartTimeDesc(ownerId, now, now, page);
            case PAST:
                return bookingRepository.findAllByItemOwnerIdAndEndTimeBeforeOrderByStartTimeDesc(ownerId, now, page);
            case FUTURE:
                return bookingRepository.findAllByItemOwnerIdAndStartTimeAfterOrderByStartTimeDesc(ownerId, now, page);
            case WAITING:
                return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartTimeDesc(ownerId, BookingStatus.WAITING, page);
            case REJECTED:
                return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartTimeDesc(ownerId, BookingStatus.REJECTED, page);
            default:
                throw new UnsupportedOperationException(String.format("Unknown state: %s", state));
        }
    }

    @Override
    public Booking get(Long bookingId, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(String.format("User %d not found", userId));
        }
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Booking %d not found", bookingId)));
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new EntityNotFoundException(String.format("Booking %d not found", bookingId));
        }
        return booking;
    }

    protected State getState(String stringState) {
        State state;
        try {
            state = State.valueOf(stringState);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedOperationException(String.format("Unknown state: %s", stringState));
        }
        return state;
    }
}
