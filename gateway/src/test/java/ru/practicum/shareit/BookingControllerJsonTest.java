package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.dto.State;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
class BookingControllerJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Валидная сериализация NewBookingRequest")
    void testNewBookingRequestSerialization() throws Exception {
        NewBookingRequest validRequest = NewBookingRequest.builder()
                .start(LocalDateTime.of(2025, 6, 1, 10, 0))
                .end(LocalDateTime.of(2025, 6, 2, 10, 0))
                .itemId(1L)
                .build();

        String json = objectMapper.writeValueAsString(validRequest);
        NewBookingRequest deserialized = objectMapper.readValue(json, NewBookingRequest.class);

        assertEquals(LocalDateTime.of(2025, 6, 1, 10, 0), deserialized.getStart());
        assertEquals(LocalDateTime.of(2025, 6, 2, 10, 0), deserialized.getEnd());
        assertEquals(1L, deserialized.getItemId());
    }

    @Test
    @DisplayName("Валидная сериализация State")
    void testStateSerialization() throws Exception {
        State state = State.ALL;
        String json = objectMapper.writeValueAsString(state);
        State deserialized = objectMapper.readValue(json, State.class);

        assertEquals(State.ALL, deserialized);
    }

    @Test
    @DisplayName("Валидная сериализация всех состояний State")
    void testAllStatesSerialization() throws Exception {
        for (State state : State.values()) {
            String json = objectMapper.writeValueAsString(state);
            State deserialized = objectMapper.readValue(json, State.class);
            assertEquals(state, deserialized);
        }
    }
}
