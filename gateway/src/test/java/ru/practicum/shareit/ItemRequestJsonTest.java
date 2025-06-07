package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.request.dto.NewRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
class ItemRequestJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Валидная сериализация NewRequest")
    void testValidNewRequestSerialization() throws Exception {
        NewRequest validRequest = NewRequest.builder()
                .description("Описание запроса")
                .build();

        String json = objectMapper.writeValueAsString(validRequest);
        NewRequest deserialized = objectMapper.readValue(json, NewRequest.class);

        assertEquals("Описание запроса", deserialized.getDescription());
    }

    @Test
    @DisplayName("Проверка десериализации JSON")
    void testNewRequestDeserialization() throws Exception {
        String json = "{\"description\":\"Описание запроса\"}";

        NewRequest deserialized = objectMapper.readValue(json, NewRequest.class);

        assertEquals("Описание запроса", deserialized.getDescription());
    }
}

