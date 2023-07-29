package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserServiceImplIntegrationTest {
    @Autowired
    UserService userService;

    @Test
    void createAndGetUser() {
        String name = "name";
        String email = "name@example.com";

        User user = userService.create(name, email);
        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
        assertNotNull(user.getId());

        User userGet = userService.get(user.getId());
        assertEquals(user, userGet);
    }

    @Test
    void updateUser() {
        User user = userService.create("user", "email@example.com");
        User updatedUser = userService.update(user.getId(), "other", null);
        assertEquals("other", updatedUser.getName());
        assertEquals(user.getEmail(), updatedUser.getEmail());
        assertEquals(user.getId(), updatedUser.getId());

        updatedUser = userService.update(user.getId(), "other other", "other@example.com");
        assertEquals("other other", updatedUser.getName());
        assertEquals("other@example.com", updatedUser.getEmail());
        assertEquals(user.getId(), updatedUser.getId());

        User getUser = userService.get(user.getId());
        assertEquals(updatedUser, getUser);
    }

    @Test
    void deleteUser() {
        User user = userService.create("user", "email@example.com");
        userService.delete(user.getId());
        assertThrows(EntityNotFoundException.class, () -> userService.get(user.getId()));
    }
}
