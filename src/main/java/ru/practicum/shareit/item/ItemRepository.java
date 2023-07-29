package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerIdOrderById(Long id, Pageable page);

    List<Item> findAllByRequestId(Long requestId);

    List<Item> findAllByRequestIdIn(List<Long> requestIds);

    @Query(value = "select * from items " +
            "where (name ilike concat('%',?1,'%') or description ilike concat('%',?1,'%')) " +
            "and available = true", nativeQuery = true)
    List<Item> findAllByTextOrderById(String text, Pageable page);
}
