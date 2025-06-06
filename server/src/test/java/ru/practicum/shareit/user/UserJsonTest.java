package ru.practicum.shareit.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

@SpringBootTest
@AutoConfigureMockMvc
public class UserJsonTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Валидный JSON с корректным email")
    void testValidJson() throws Exception {
        NewUserRequest validUser = NewUserRequest.builder()
                .name("Иван Иванов")
                .email("ivan@example.com")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Проверка некорректного формата email")
    void testInvalidEmailFormat() throws Exception {
        NewUserRequest invalidUser = NewUserRequest.builder()
                .name("Иван Иванов")
                .email("некорректный_email")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }

    @Test
    @DisplayName("Проверка пустого поля name")
    void testEmptyName() throws Exception {
        NewUserRequest invalidUser = NewUserRequest.builder()
                .name("")
                .email("ivan@example.com")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }

    @Test
    @DisplayName("Проверка пустого поля email")
    void testEmptyEmail() throws Exception {
        NewUserRequest invalidUser = NewUserRequest.builder()
                .name("Иван Иванов")
                .email("")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }

    @Test
    @DisplayName("Проверка отсутствия обязательных полей")
    void testMissingFields() throws Exception {
        NewUserRequest invalidUser = new NewUserRequest();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }

    @Test
    @DisplayName("Проверка обновления с некорректным email")
    void testUpdateInvalidEmail() throws Exception {
        NewUserRequest newUser = NewUserRequest.builder()
                .name("Иван Иванов")
                .email("ivan2@example.com")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated());
        Long userId = 1L;

        UpdateUserRequest updateRequest = UpdateUserRequest.builder()
                .name("Петр Петров")
                .email("некорректный_email")
                .build();

        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }
}
