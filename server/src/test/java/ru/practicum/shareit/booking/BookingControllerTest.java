package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.exception.UnavailableItemException;
import ru.practicum.shareit.exception.UnsupportedOperationException;
import ru.practicum.shareit.fixtures.BookingFixture;
import ru.practicum.shareit.fixtures.ItemFixture;
import ru.practicum.shareit.fixtures.ItemRequestFixture;
import ru.practicum.shareit.fixtures.UserFixture;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @MockBean
    BookingService bookingService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mvc;


    User user;
    Item item;
    Booking booking;
    ItemRequest itemRequest;

    @BeforeEach
    void setup() {
        user = UserFixture.getUser();
        item = ItemFixture.getItem(user);
        itemRequest = ItemRequestFixture.getItemRequest(user);
        item.setRequest(itemRequest);
        booking = BookingFixture.getActualBooking(user, item);
    }

    @Test
    void create() throws Exception {
        when(bookingService.create(any(), any(), any(), any()))
                .thenReturn(booking);

        CreateBookingDto request = new CreateBookingDto(
                LocalDateTime.now(),
                LocalDateTime.now(),
                1L
        );

        mvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "42")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists())
                .andExpect(jsonPath("$.item").exists())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.booker").exists());
        verify(bookingService, times(1)).create(1L, request.getStart(), request.getEnd(), 42L);

        reset(bookingService);
        when(bookingService.create(any(), any(), any(), any()))
                .thenThrow(UnavailableItemException.class);
        mvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "42")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void approve() throws Exception {
        when(bookingService.approve(any(), anyBoolean(), any()))
                .thenReturn(booking);

        mvc.perform(patch("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("approved", "true")
                        .header("X-Sharer-User-Id", "42"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists())
                .andExpect(jsonPath("$.item").exists())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.booker").exists());
        verify(bookingService, times(1)).approve(1L, true, 42L);

        reset(bookingService);
        when(bookingService.approve(any(), anyBoolean(), any()))
                .thenThrow(UnsupportedOperationException.class);
        mvc.perform(patch("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("approved", "true")
                        .header("X-Sharer-User-Id", "42"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void get() throws Exception {
        when(bookingService.get(any(), any()))
                .thenReturn(booking);

        CreateBookingDto request = new CreateBookingDto(
                LocalDateTime.now(),
                LocalDateTime.now(),
                1L
        );

        mvc.perform(MockMvcRequestBuilders.get("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "42")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists())
                .andExpect(jsonPath("$.item").exists())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.booker").exists());
        verify(bookingService, times(1)).get(1L, 42L);
    }

    @Test
    void getUserBookings() throws Exception {
        when(bookingService.getUserBookings(any(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(booking));

        CreateBookingDto request = new CreateBookingDto(
                LocalDateTime.now(),
                LocalDateTime.now(),
                1L
        );

        mvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("state", "ALL")
                        .queryParam("from", "0")
                        .queryParam("size", "100")
                        .header("X-Sharer-User-Id", "42")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
        verify(bookingService, times(1)).getUserBookings("ALL", 42L, 0, 100);
    }

    @Test
    void getOwnerBookings() throws Exception {
        when(bookingService.getOwnerBookings(any(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(booking));

        CreateBookingDto request = new CreateBookingDto(
                LocalDateTime.now(),
                LocalDateTime.now(),
                1L
        );

        mvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("state", "ALL")
                        .queryParam("from", "0")
                        .queryParam("size", "100")
                        .header("X-Sharer-User-Id", "42")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
        verify(bookingService, times(1)).getOwnerBookings("ALL", 42L, 0, 100);
    }
}