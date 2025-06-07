package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NoAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@WebMvcTest(controllers = BookingController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    MockMvc mockMvc;

    UserDto owner;
    UserDto booker;
    ItemDto item;
    BookingDto bookingDto;
    List<BookingDto> bookings = new ArrayList<>();

    @BeforeEach
    void setUp() {
        owner = UserDto.builder()
                .id(1L)
                .name("owner")
                .email("owner@mail.ru")
                .build();

        booker = UserDto.builder()
                .id(2L)
                .name("booker")
                .email("booker@mail.ru")
                .build();

        item = ItemDto.builder()
                .id(1L)
                .name("стол")
                .description("стол походный")
                .available(true)
                .owner(owner)
                .build();

        bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2025, 10, 3, 11, 30, 10))
                .end(LocalDateTime.of(2025, 11, 3, 11, 30, 10))
                .status(Status.WAITING)
                .item(item)
                .booker(booker)
                .build();

        bookings = List.of(bookingDto);
    }

    @DisplayName("Добавление бронирования")
    @Test
    void addBookingTest() throws Exception {
        NewBookingRequest request = NewBookingRequest.builder()
                .start(LocalDateTime.of(2025, 10, 3, 11, 30, 10))
                .end(LocalDateTime.of(2025, 11, 3, 11, 30, 10))
                .itemId(1L)
                .build();
        when(bookingService.add(request, 2L)).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2L)
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @DisplayName("Обновление статуса")
    @Test
    void updateStatusBookingTest() throws Exception {
        BookingDto updateBooking = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2025, 10, 3, 11, 30, 10))
                .end(LocalDateTime.of(2025, 11, 3, 11, 30, 10))
                .status(Status.APPROVED)
                .item(item)
                .booker(booker)
                .build();
        when(bookingService.updateStatus(any(Long.class), any(Long.class), any(Boolean.class)))
                .thenReturn(updateBooking);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 2L)
                        .param("approved", "true")
                        .content(mapper.writeValueAsString(updateBooking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updateBooking.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(updateBooking.getStart().toString())))
                .andExpect(jsonPath("$.end", is(updateBooking.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(updateBooking.getStatus().toString())));
    }

    @DisplayName("Получение по id")
    @Test
    void getBookingByIdTest() throws Exception {
        when(bookingService.findById(any(Long.class), any(Long.class))).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 2L)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @DisplayName("Получение по id владельца вещи")
    @Test
    void findByOwnerIdTest() throws Exception {
        when(bookingService.findByOwnerId(any(Long.class), any(State.class))).thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .content(mapper.writeValueAsString(bookings))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())));
    }

    @DisplayName("Получение по id booker")
    @Test
    void findByBookerIdTest() throws Exception {
        when(bookingService.findByBookerId(2L, State.ALL)).thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 2L)
                        .param("state", "ALL")
                        .content(mapper.writeValueAsString(bookings))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())));
    }

    @DisplayName("Получение несуществующего бронирования")
    @Test
    void findNonExistentBooking() throws Exception {
        long userId = 1L;
        long bookingId = 999L;

        when(bookingService.findById(userId, bookingId))
                .thenThrow(new NotFoundException("Booking not found"));

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Booking not found")));
    }

    @DisplayName("Получение бронирования для несуществующего пользователя")
    @Test
    void findNonExistentUser() throws Exception {
        long userId = 999L;

        when(bookingService.findByBookerId(userId, State.ALL))
                .thenThrow(new NotFoundException("Booking not found"));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Booking not found")));
    }

    @DisplayName("Обновление статуса без права доступа")
    @Test
    void noAccessExceptionTest() throws Exception {
        long userId = 999L;
        long bookingId = 1L;

        when(bookingService.updateStatus(userId, bookingId, true))
                .thenThrow(new NoAccessException("Access denied"));

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isForbidden())
                .andExpect(content().string(containsString("Access denied")));
    }

}
