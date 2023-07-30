package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.fixtures.UserFixture;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemRepositoryTest {
    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    void findAllByTextOrderById() {
        User user = userRepository.save(UserFixture.getUser());
        itemRepository.save(new Item(1L, "SeArCh", "desc", true, user, null));
        itemRepository.save(new Item(2L, "name", "SeaRCH", true, user, null));
        itemRepository.save(new Item(3L, "name", "desc", true, user, null));
        itemRepository.save(new Item(4L, "other search name", "desc", true, user, null));

        List<Item> result = itemRepository.findAllByTextOrderById("search", PageRequest.of(0, 100));
        assertEquals(3, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        assertEquals(4L, result.get(2).getId());
    }
}