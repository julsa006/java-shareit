package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartTimeDesc(Long bookerId);

    List<Booking> findAllByItemOwnerIdOrderByStartTimeDesc(Long ownerId);

    @Query("select b from Booking as b " +
            "where b.booker.id = ?1 " +
            "and b.status = ?2 " +
            "order by b.startTime desc")
    List<Booking> findUsersByStatus(Long bookerId, BookingStatus status);

    @Query("select b from Booking as b " +
            "where b.booker.id = ?1 " +
            "and b.startTime > now() " +
            "order by b.startTime desc")
    List<Booking> findUsersFuture(Long bookerId);

    @Query("select b from Booking as b " +
            "where b.booker.id = ?1 " +
            "and b.startTime < now() " +
            "and b.endTime > now() " +
            "order by b.startTime desc")
    List<Booking> findUsersCurrent(Long bookerId);

    @Query("select b from Booking as b " +
            "where b.booker.id = ?1 " +
            "and b.endTime < now() " +
            "order by b.startTime desc")
    List<Booking> findUsersPast(Long bookerId);


    @Query("select b from Booking as b " +
            "join b.item as i " +
            "where i.owner.id = ?1 " +
            "and b.status = ?2 " +
            "order by b.startTime desc")
    List<Booking> findOwnersByStatus(Long bookerId, BookingStatus status);


    @Query("select b from Booking as b " +
            "join b.item as i " +
            "where i.owner.id = ?1 " +
            "and b.startTime > now() " +
            "order by b.startTime desc")
    List<Booking> findOwnersFuture(Long bookerId);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "where i.owner.id = ?1 " +
            "and b.startTime < now() " +
            "and b.endTime > now() " +
            "order by b.startTime desc")
    List<Booking> findOwnersCurrent(Long bookerId);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "where i.owner.id = ?1 " +
            "and b.endTime < now() " +
            "order by b.startTime desc")
    List<Booking> findOwnersPast(Long bookerId);

    @Query(value = "select * from bookings as b " +
            "where b.item_id = ?1 " +
            "and b.status = 'APPROVED' " +
            "and b.start_time < now() " +
            "order by b.start_time desc " +
            "limit 1", nativeQuery = true)
    Booking findLastBooking(Long itemId);

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
