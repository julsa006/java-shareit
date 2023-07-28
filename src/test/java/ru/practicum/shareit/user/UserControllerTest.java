package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.fixtures.UserFixture;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @MockBean
    UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mvc;

    @Test
    void create() throws Exception {
        when(userService.create(any(), any())).thenReturn(UserFixture.getUser());

        CreateUserDto createUserDto = new CreateUserDto(
                "name",
                "name@example.com"
        );

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.email").isString());
    }

    @Test
    void findAll() throws Exception {
        when(userService.getAll()).thenReturn(
                List.of(UserFixture.getUser(), UserFixture.getUser())
        );

        mvc.perform(MockMvcRequestBuilders.get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void update() throws Exception {
        when(userService.update(any(), any(), any())).thenReturn(
                UserFixture.getUser()
        );
        mvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(new UpdateUserDto("new name", "new@email.com")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.id").exists());
        verify(userService, times(1)).update(1L, "new name", "new@email.com");

        reset(userService);
        mvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(
                                new UpdateUserDto("new name", "wrong email"))
                        ).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
        verify(userService, never()).update(any(), any(), any());
    }

    @Test
    void get() throws Exception {
        when(userService.get(any())).thenReturn(
                UserFixture.getUser()
        );
        mvc.perform(MockMvcRequestBuilders.get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.id").exists());
        verify(userService, times(1)).get(1L);
    }

    @Test
    void delete() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(userService, times(1)).delete(1L);
    }
}