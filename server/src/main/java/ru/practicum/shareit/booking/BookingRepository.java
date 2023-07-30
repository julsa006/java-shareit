package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartTimeDesc(Long bookerId, Pageable page);

    List<Booking> findAllByItemOwnerIdOrderByStartTimeDesc(Long ownerId, Pageable page);

    List<Booking> findAllByBookerIdAndStatusOrderByStartTimeDesc(Long bookerId, BookingStatus status, Pageable page);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartTimeDesc(Long bookerId, BookingStatus status, Pageable page);

    List<Booking> findAllByBookerIdAndStartTimeAfterOrderByStartTimeDesc(Long bookerId, LocalDateTime startTime, Pageable page);

    List<Booking> findAllByBookerIdAndStartTimeBeforeAndEndTimeAfterOrderByStartTimeDesc(Long bookerId, LocalDateTime startTime, LocalDateTime endTime, Pageable page);

    List<Booking> findAllByBookerIdAndEndTimeBeforeOrderByStartTimeDesc(Long bookerId, LocalDateTime startTime, Pageable page);

    List<Booking> findAllByItemOwnerIdAndStartTimeAfterOrderByStartTimeDesc(Long ownerId, LocalDateTime startTime, Pageable page);

    List<Booking> findAllByItemOwnerIdAndStartTimeBeforeAndEndTimeAfterOrderByStartTimeDesc(Long bookerId, LocalDateTime startTime, LocalDateTime endTime, Pageable page);

    List<Booking> findAllByItemOwnerIdAndEndTimeBeforeOrderByStartTimeDesc(Long bookerId, LocalDateTime endTime, Pageable page);

    @Query(value = "select * from bookings as b " +
            "where b.item_id = ?1 " +
            "and b.status = 'APPROVED' " +
            "and b.start_time < now() " +
            "order by b.start_time desc " +
            "limit 1", nativeQuery = true)
    Booking findLastBooking(Long itemId);

    @Query(value = "select b1.* from bookings as b1 " +
            "join (select b2.item_id as item_id, max(b2.start_time) as max_time " +
            "from bookings as b2 " +
            "where b2.item_id in ?1 " +
            "and b2.status = 'APPROVED' " +
            "and b2.start_time < now() " +
            "group by b2.item_id) as b3 " +
            "on b1.item_id = b3.item_id " +
            "and b1.start_time = b3.max_time " +
            "where b1.status = 'APPROVED' ", nativeQuery = true)
    List<Booking> findLastBookings(List<Long> itemIds);

    @Query(value = "select b1.* from bookings as b1 " +
            "join (select b2.item_id as item_id, min(b2.start_time) as min_time " +
            "from bookings as b2 " +
            "where b2.item_id in ?1 " +
            "and b2.status = 'APPROVED' " +
            "and b2.start_time > now() " +
            "group by b2.item_id) as b3 " +
            "on b1.item_id = b3.item_id " +
            "and b1.start_time = b3.min_time " +
            "where b1.status = 'APPROVED' ", nativeQuery = true)
    List<Booking> findNextBookings(List<Long> itemIds);

    @Query(value = "select * from bookings as b " +
            "where b.item_id = ?1 " +
            "and b.status = 'APPROVED' " +
            "and b.start_time > now() " +
            "order by b.start_time " +
            "limit 1", nativeQuery = true)
    Booking findNextBooking(Long itemId);

    @Query(value = "select count(*) from bookings as b " +
            "where b.item_id = ?1 " +
            "and b.end_time < now() " +
            "and b.booker_id = ?2 ", nativeQuery = true)
    int countItemUserBookings(Long itemId, Long bookerId);

}
