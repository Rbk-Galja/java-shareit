package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import static org.junit.jupiter.api.Assertions.*;

@JsonTest
class ItemControllerJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Валидная сериализация NewItemRequest")
    void testNewItemRequestSerialization() throws Exception {
        NewItemRequest validRequest = NewItemRequest.builder()
                .name("Вещь")
                .description("Описание вещи")
                .available(true)
                .requestId(1L)
                .build();

        String json = objectMapper.writeValueAsString(validRequest);
        NewItemRequest deserialized = objectMapper.readValue(json, NewItemRequest.class);

        assertEquals("Вещь", deserialized.getName());
        assertEquals("Описание вещи", deserialized.getDescription());
        assertTrue(deserialized.getAvailable());
        assertEquals(1L, deserialized.getRequestId());
    }

    @Test
    @DisplayName("Валидная сериализация UpdateItemRequest")
    void testUpdateItemRequestSerialization() throws Exception {
        UpdateItemRequest validRequest = UpdateItemRequest.builder()
                .name("Обновленное название")
                .description("Обновленное описание")
                .available(false)
                .build();

        String json = objectMapper.writeValueAsString(validRequest);
        UpdateItemRequest deserialized = objectMapper.readValue(json, UpdateItemRequest.class);

        assertEquals("Обновленное название", deserialized.getName());
        assertEquals("Обновленное описание", deserialized.getDescription());
        assertFalse(deserialized.getAvailable());
    }

    @Test
    @DisplayName("Проверка частичной сериализации UpdateItemRequest")
    void testPartialUpdateItemRequestSerialization() throws Exception {
        UpdateItemRequest partialRequest = UpdateItemRequest.builder()
                .name("Новое название")
                .build();

        String json = objectMapper.writeValueAsString(partialRequest);
        UpdateItemRequest deserialized = objectMapper.readValue(json, UpdateItemRequest.class);

        assertEquals("Новое название", deserialized.getName());
        assertNull(deserialized.getDescription());
        assertNull(deserialized.getAvailable());
    }

    @Test
    @DisplayName("Валидная сериализация NewCommentRequest")
    void testNewCommentRequestSerialization() throws Exception {
        NewCommentRequest validRequest = NewCommentRequest.builder()
                .text("Комментарий к вещи")
                .build();

        String json = objectMapper.writeValueAsString(validRequest);
        NewCommentRequest deserialized = objectMapper.readValue(json, NewCommentRequest.class);

        assertEquals("Комментарий к вещи", deserialized.getText());
    }
}

