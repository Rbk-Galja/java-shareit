package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
@AutoConfigureMockMvc
public class UserControllerJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Валидная сериализация NewUserRequest")
    void testNewUserRequestSerialization() throws Exception {
        NewUserRequest validRequest = NewUserRequest.builder()
                .name("Иван Иванов")
                .email("ivan@example.com")
                .build();

        String json = objectMapper.writeValueAsString(validRequest);
        NewUserRequest deserialized = objectMapper.readValue(json, NewUserRequest.class);

        assertEquals("Иван Иванов", deserialized.getName());
        assertEquals("ivan@example.com", deserialized.getEmail());
    }

    @Test
    @DisplayName("Валидная сериализация UpdateUserRequest")
    void testUpdateUserRequestSerialization() throws Exception {
        UpdateUserRequest updateRequest = UpdateUserRequest.builder()
                .name("Новое имя")
                .email("new@email.com")
                .build();

        String json = objectMapper.writeValueAsString(updateRequest);
        UpdateUserRequest deserialized = objectMapper.readValue(json, UpdateUserRequest.class);

        assertEquals("Новое имя", deserialized.getName());
        assertEquals("new@email.com", deserialized.getEmail());
    }

}
