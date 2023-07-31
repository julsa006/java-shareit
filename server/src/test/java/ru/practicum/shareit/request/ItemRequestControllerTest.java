package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.fixtures.BookingFixture;
import ru.practicum.shareit.fixtures.ItemFixture;
import ru.practicum.shareit.fixtures.ItemRequestFixture;
import ru.practicum.shareit.fixtures.UserFixture;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @MockBean
    ItemRequestService itemRequestService;

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
        when(itemRequestService.create(any(), any()))
                .thenReturn(itemRequest);

        CreateItemRequestDto request = new CreateItemRequestDto("desc");

        mvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "42")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.created").exists());
        verify(itemRequestService, times(1)).create("desc", 42L);
    }

    @Test
    void getOwn() throws Exception {
        when(itemRequestService.getOwn(any()))
                .thenReturn(List.of(new ItemRequestWithItems(itemRequest, List.of(item))));


        mvc.perform(MockMvcRequestBuilders.get("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "42"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
        verify(itemRequestService, times(1)).getOwn(42L);
    }

    @Test
    void get() throws Exception {
        when(itemRequestService.get(any(), any()))
                .thenReturn(new ItemRequestWithItems(itemRequest, List.of(item)));

        CreateItemRequestDto request = new CreateItemRequestDto("desc");

        mvc.perform(MockMvcRequestBuilders.get("/requests/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "42")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.created").exists());
        verify(itemRequestService, times(1)).get(1L, 42L);
    }

    @Test
    void getAll() throws Exception {
        when(itemRequestService.getAll(anyInt(), anyInt(), any()))
                .thenReturn(List.of(new ItemRequestWithItems(itemRequest, List.of(item))));


        mvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("from", "0")
                        .queryParam("size", "100")
                        .header("X-Sharer-User-Id", "42"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
        verify(itemRequestService, times(1)).getAll(0, 100, 42L);
    }
}