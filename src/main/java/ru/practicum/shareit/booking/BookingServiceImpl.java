package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnavailableException;
import ru.practicum.shareit.exception.UnsupportedException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {
    BookingRepository bookingRepository;
    UserRepository userRepository;
    ItemRepository itemRepository;

    @Override
    public Booking create(CreateBookingDto booking, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User %d not found", userId)));
        Item item = itemRepository.findById(booking.getItemId())
                .orElseThrow(() -> new NotFoundException(String.format("Item %d not found", booking.getItemId())));
        if (userId.equals(item.getId())) {
            throw new NotFoundException(String.format("Item %d not found", booking.getItemId()));
        }
        if (!item.isAvailable()) {
            throw new UnavailableException(String.format("Item %d unavailable", item.getId()));
        }
        return bookingRepository.save(new Booking(null, booking.getStart(), booking.getEnd(), item,
                user, BookingStatus.WAITING));
    }

    @Override
    public Booking approve(Long bookingId, boolean approved, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Booking %d not found", bookingId)));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException(String.format("Booking %d not found", bookingId));
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new UnsupportedException(String.format("Booking %d status not waiting", bookingId));
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return bookingRepository.save(booking);
    }

    @Override
    public List<Booking> getUserBookings(State state, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User %d not found", userId));
        }
        switch (state) {
            case ALL:
                return bookingRepository.findAllByBookerIdOrderByStartTimeDesc(userId);
            case CURRENT:
                return bookingRepository.findUsersCurrent(userId);
            case PAST:
                return bookingRepository.findUsersPast(userId);
            case FUTURE:
                return bookingRepository.findUsersFuture(userId);
            case WAITING:
                return bookingRepository.findUsersByStatus(userId, BookingStatus.WAITING);
            case REJECTED:
                return bookingRepository.findUsersByStatus(userId, BookingStatus.REJECTED);
            default:
                throw new UnsupportedException(String.format("Unknown state: %s", state));
        }
    }

    @Override
    public List<Booking> getOwnerBookings(State state, Long ownerId) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException(String.format("User %d not found", ownerId));
        }
        switch (state) {
            case ALL:
                return bookingRepository.findAllByItemOwnerIdOrderByStartTimeDesc(ownerId);
            case CURRENT:
                return bookingRepository.findOwnersCurrent(ownerId);
            case PAST:
                return bookingRepository.findOwnersPast(ownerId);
            case FUTURE:
                return bookingRepository.findOwnersFuture(ownerId);
            case WAITING:
                return bookingRepository.findOwnersByStatus(ownerId, BookingStatus.WAITING);
            case REJECTED:
                return bookingRepository.findOwnersByStatus(ownerId, BookingStatus.REJECTED);
            default:
                throw new UnsupportedException(String.format("Unknown state: %s", state));
        }
    }

    @Override
    public Booking get(Long bookingId, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User %d not found", userId));
        }
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Booking %d not found", bookingId)));
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException(String.format("Booking %d not found", bookingId));
        }
        return booking;
    }
}
