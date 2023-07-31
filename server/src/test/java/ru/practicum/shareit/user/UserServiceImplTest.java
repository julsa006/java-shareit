package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.fixtures.UserFixture;
import ru.practicum.shareit.utils.DBMockTest;

import java.util.Optional;

import static org.mockito.Mockito.*;

@SpringJUnitConfig({UserServiceImpl.class})
class UserServiceImplTest extends DBMockTest {
    @Autowired
    UserService userService;

    @Test
    void testUserUpdate() {
        when(userRepositoryMock.findById(1L)).thenAnswer(invocationOnMock -> Optional.of(UserFixture.getUser(1L, "original@example.com")));

        User expected = UserFixture.getUser(1L, "original@example.com");
        userService.update(1L, null, null);
        verify(userRepositoryMock, times(1)).save(expected);

        userService.update(1L, "new name", null);
        expected = UserFixture.getUser(1L, "original@example.com");
        expected.setName("new name");
        verify(userRepositoryMock, times(1)).save(expected);

        userService.update(1L, null, "new@email.com");
        expected = UserFixture.getUser(1L, "original@example.com");
        expected.setEmail("new@email.com");
        verify(userRepositoryMock, times(1)).save(expected);

        userService.update(1L, "new name", "new@email.com");
        expected = UserFixture.getUser(1L, "original@example.com");
        expected.setEmail("new@email.com");
        expected.setName("new name");
        verify(userRepositoryMock, times(1)).save(expected);
    }

}