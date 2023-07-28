package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.fixtures.ItemFixture;
import ru.practicum.shareit.fixtures.UserFixture;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoTest {
    @Autowired
    JacksonTester<BookingDto> json;

    @Test
    void testTimeFormat() throws IOException {
        User user = UserFixture.getUser();
        UserDto userDto = UserMapper.toUserDto(user);

        Item item = ItemFixture.getItem(user);
        ItemDto itemDto = ItemMapper.toItemDto(item);

        BookingDto bookingDto = new BookingDto(
                1L,
                LocalDateTime.of(2023, 12, 13, 1, 1),
                LocalDateTime.of(2023, 12, 14, 1, 1),
                itemDto,
                userDto,
                BookingStatus.APPROVED
        );

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-12-13T01:01:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-12-14T01:01:00");
    }

}