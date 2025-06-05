package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ItemJsonTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("Корректный JSON с заполненными полями и без request")
    @Test
    void testValidJsonWithoutRequest() throws Exception {
        UserDto owner = UserDto.builder()
                .id(1L)
                .name("Иоан я")
                .email("ioann09987@ya.ru")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owner)))
                .andExpect(status().isCreated());

        NewItemRequest request = NewItemRequest.builder()
                .name("вещь")
                .description("штука огонь")
                .available(true)
                .build();

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @DisplayName("Корректный JSON с заполненными полями и c request")
    @Test
    void testValidItemWithRequest() throws Exception {
        UserDto requestUser = UserDto.builder()
                .id(1L)
                .name("Катюха")
                .email("kat@ya.ru")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUser)))
                .andExpect(status().isCreated());

        NewRequest requestIem = NewRequest.builder().description("Дайте дрель подрелить").build();

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestIem)))
                .andExpect(status().isCreated());

        UserDto owner = UserDto.builder()
                .id(2L)
                .name("Иоан я")
                .email("ioann54@ya.ru")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owner)))
                .andExpect(status().isCreated());

        NewItemRequest request = NewItemRequest.builder()
                .name("вещь")
                .description("штука огонь")
                .available(true)
                .requestId(1L)
                .build();

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @DisplayName("Создание вещи с пустым названием")
    @Test
    void createItemWithoutName() throws Exception {
        UserDto owner = UserDto.builder()
                .id(1L)
                .name("Иоан я")
                .email("ioann59@ya.ru")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owner)))
                .andExpect(status().isCreated());

        NewItemRequest invalidItem = NewItemRequest.builder()
                .name("")
                .description("штука огонь")
                .available(true)
                .build();

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidItem)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }

    @DisplayName("Создание вещи с пустым описанием")
    @Test
    void createItemWithoutDescription() throws Exception {
        UserDto owner = UserDto.builder()
                .id(1L)
                .name("Иоан я")
                .email("ioann8909@ya.ru")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owner)))
                .andExpect(status().isCreated());

        NewItemRequest invalidItem = NewItemRequest.builder()
                .name("вещь")
                .description("")
                .available(true)
                .build();

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidItem)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }

    @DisplayName("Создание вещи без статуса")
    @Test
    void createItemWithoutAvailable() throws Exception {
        UserDto owner = UserDto.builder()
                .id(1L)
                .name("Иоан я")
                .email("ioann3@ya.ru")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owner)))
                .andExpect(status().isCreated());

        NewItemRequest invalidItem = NewItemRequest.builder()
                .name("вещь")
                .description("штука огонь")
                .build();

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidItem)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }
}
