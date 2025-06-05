package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BookingJsonTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("Корректное бронирование с заполненными полями")
    @Test
    void createValidBooking() throws Exception {
        UserDto owner = UserDto.builder()
                .id(1L)
                .name("Иоан я")
                .email("ioann090987@ya.ru")
                .build();

        ItemDto item = ItemDto.builder()
                .id(1L)
                .name("вещь")
                .description("штука огонь")
                .available(true)
                .owner(owner)
                .build();

        UserDto booker = UserDto.builder()
                .id(2L)
                .name("Иоан я")
                .email("ioann897654@ya.ru")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owner)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booker)))
                .andExpect(status().isCreated());

        NewBookingRequest request = NewBookingRequest.builder()
                .start(LocalDateTime.of(2025, 10, 3, 11, 30, 10))
                .end(LocalDateTime.of(2025, 11, 3, 11, 30, 10))
                .itemId(1L)
                .build();

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @DisplayName("Бронирование с пустым ItemId")
    @Test
    void createBookingWithoutItemId() throws Exception {
        UserDto owner = UserDto.builder()
                .id(1L)
                .name("Иоан я")
                .email("ioann345@ya.ru")
                .build();

        ItemDto item = ItemDto.builder()
                .id(1L)
                .name("вещь")
                .description("штука огонь")
                .available(true)
                .owner(owner)
                .build();

        UserDto booker = UserDto.builder()
                .id(2L)
                .name("Иоан я")
                .email("ioann19@ya.ru")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owner)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booker)))
                .andExpect(status().isCreated());

        NewBookingRequest request = NewBookingRequest.builder()
                .start(LocalDateTime.of(2025, 10, 3, 11, 30, 10))
                .end(LocalDateTime.of(2025, 11, 3, 11, 30, 10))
                .build();

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }

    @DisplayName("Бронирование со стартом в прошлом")
    @Test
    void createBookingWithStartInPast() throws Exception {
        UserDto owner = UserDto.builder()
                .id(1L)
                .name("Иоан я")
                .email("ioann3459@ya.ru")
                .build();

        ItemDto item = ItemDto.builder()
                .id(1L)
                .name("вещь")
                .description("штука огонь")
                .available(true)
                .owner(owner)
                .build();

        UserDto booker = UserDto.builder()
                .id(2L)
                .name("Иоан я")
                .email("ioann195@ya.ru")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owner)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booker)))
                .andExpect(status().isCreated());

        NewBookingRequest request = NewBookingRequest.builder()
                .start(LocalDateTime.of(2025, 3, 3, 11, 30, 10))
                .end(LocalDateTime.of(2025, 11, 3, 11, 30, 10))
                .itemId(1L)
                .build();

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }

    @DisplayName("Бронирование с окончанием в прошлом")
    @Test
    void createBookingWithEndInPresent() throws Exception {
        UserDto owner = UserDto.builder()
                .id(1L)
                .name("Иоан я")
                .email("ioann345987@ya.ru")
                .build();

        ItemDto item = ItemDto.builder()
                .id(1L)
                .name("вещь")
                .description("штука огонь")
                .available(true)
                .owner(owner)
                .build();

        UserDto booker = UserDto.builder()
                .id(2L)
                .name("Иоан я")
                .email("ioann19987654@ya.ru")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owner)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booker)))
                .andExpect(status().isCreated());

        NewBookingRequest request = NewBookingRequest.builder()
                .start(LocalDateTime.of(2025, 10, 3, 11, 30, 10))
                .end(LocalDateTime.of(2025, 3, 3, 11, 30, 10))
                .itemId(1L)
                .build();

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }
}
