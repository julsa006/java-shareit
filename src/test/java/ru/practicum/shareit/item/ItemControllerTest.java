package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.CommentWithoutBookingException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.fixtures.CommentFixture;
import ru.practicum.shareit.fixtures.ItemFixture;
import ru.practicum.shareit.fixtures.ItemRequestFixture;
import ru.practicum.shareit.fixtures.UserFixture;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @MockBean
    ItemService itemService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mvc;

    @Test
    void create() throws Exception {
        User user = UserFixture.getUser();
        Item item = ItemFixture.getItem(user);
        item.setRequest(ItemRequestFixture.getItemRequest(user));

        when(itemService.create(any(), any(), anyBoolean(), any(), any()))
                .thenReturn(item);

        CreateItemDto request = new CreateItemDto(
                "name",
                "desc",
                true,
                1L
        );

        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "42")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.owner").exists())
                .andExpect(jsonPath("$.requestId").exists());
        verify(itemService, times(1)).create("name", "desc", true, 1L, 42L);
    }

    @Test
    void update() throws Exception {
        User user = UserFixture.getUser();
        Item item = ItemFixture.getItem(user);
        item.setRequest(ItemRequestFixture.getItemRequest(user));

        when(itemService.update(any(), any(), any(), any(), any()))
                .thenReturn(item);

        UpdateItemDto request = new UpdateItemDto(
                "name",
                "desc",
                true
        );

        mvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "42")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.owner").exists())
                .andExpect(jsonPath("$.requestId").exists());
        verify(itemService, times(1)).update(1L, "name", "desc", true, 42L);

        when(itemService.update(any(), any(), any(), any(), any()))
                .thenThrow(AccessDeniedException.class);
        mvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "42")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void get() throws Exception {
        User user = UserFixture.getUser();
        Item item = ItemFixture.getItem(user);
        item.setRequest(ItemRequestFixture.getItemRequest(user));

        when(itemService.get(any(), any()))
                .thenReturn(new FullItem(item, null, null, List.of()));

        UpdateItemDto request = new UpdateItemDto(
                "name",
                "desc",
                true
        );

        mvc.perform(MockMvcRequestBuilders.get("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "42")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.owner").exists())
                .andExpect(jsonPath("$.requestId").exists());
        verify(itemService, times(1)).get(1L, 42L);

        reset(itemService);
        when(itemService.get(any(), any()))
                .thenThrow(EntityNotFoundException.class);
        mvc.perform(MockMvcRequestBuilders.get("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "42")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());

        reset(itemService);
        when(itemService.get(any(), any()))
                .thenThrow(NullPointerException.class);
        mvc.perform(MockMvcRequestBuilders.get("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "42")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void getUserItems() throws Exception {
        User user = UserFixture.getUser();
        Item item = ItemFixture.getItem(user);
        item.setRequest(ItemRequestFixture.getItemRequest(user));

        when(itemService.getUserItems(any(), anyInt(), anyInt()))
                .thenReturn(List.of(new FullItem(item, null, null, List.of())));

        mvc.perform(MockMvcRequestBuilders.get("/items")
                        .queryParam("from", "0")
                        .queryParam("size", "100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "42"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(itemService, times(1)).getUserItems(42L, 0, 100);
    }

    @Test
    void search() throws Exception {
        User user = UserFixture.getUser();
        Item item = ItemFixture.getItem(user);
        item.setRequest(ItemRequestFixture.getItemRequest(user));

        when(itemService.get(any(), any()))
                .thenReturn(new FullItem(item, null, null, List.of()));

        mvc.perform(MockMvcRequestBuilders.get("/items/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("text", "search")
                        .queryParam("from", "0")
                        .queryParam("size", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
        verify(itemService, times(1)).searchItems("search", 0, 100);
    }

    @Test
    void createComment() throws Exception {
        User user = UserFixture.getUser();
        Item item = ItemFixture.getItem(user);
        item.setRequest(ItemRequestFixture.getItemRequest(user));

        when(itemService.createComment(any(), any(), any()))
                .thenReturn(CommentFixture.getComment(item, user));

        CreateCommentDto request = new CreateCommentDto("text");

        mvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "42")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.text").exists())
                .andExpect(jsonPath("$.authorName").exists())
                .andExpect(jsonPath("$.created").exists());
        verify(itemService, times(1)).createComment("text", 1L, 42L);

        reset(itemService);
        when(itemService.createComment(any(), any(), any()))
                .thenThrow(CommentWithoutBookingException.class);
        mvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "42")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }
}