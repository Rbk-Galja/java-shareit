package ru.practicum.shareit.request;

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
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestListAnswerDto;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@WebMvcTest(controllers = ItemRequestController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    MockMvc mockMvc;

    UserDto user;
    ItemRequestDto requestDto;
    List<ItemDto> items;
    ItemRequestListAnswerDto itemRequestListAnswerDto;
    ItemDto itemDto;
    UserDto owner;
    NewRequest newRequest;
    List<ItemRequestListAnswerDto> requests;
    List<ItemRequestDto> requestsDto;

    @BeforeEach
    void setUp() {
        user = UserDto.builder()
                .id(1L)
                .name("name")
                .email("email@mail.ru")
                .build();

        requestDto = ItemRequestDto.builder()
                .id(1L)
                .description("description")
                .requestor(user)
                .created(LocalDateTime.of(2025, 10, 3, 11, 30, 10))
                .build();

        newRequest = NewRequest.builder().description("description").build();

        owner = UserDto.builder()
                .id(2L)
                .name("owner")
                .email("owner@mail.ru")
                .build();

        itemDto = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .owner(owner)
                .available(true)
                .itemRequest(requestDto)
                .build();

        items = List.of(itemDto);

        itemRequestListAnswerDto = ItemRequestListAnswerDto.builder()
                .id(1L)
                .description("description")
                .requesterName(user.getName())
                .created(LocalDateTime.of(2025, 10, 3, 11, 30, 10))
                .items(items)
                .build();

        requests = List.of(itemRequestListAnswerDto);
        requestsDto = List.of(requestDto);
    }

    @DisplayName("Создание запроса вещи")
    @Test
    void createRequestTest() throws Exception {
        when(itemRequestService.add(1L, newRequest)).thenReturn(requestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(requestDto.getCreated().toString())));
    }

    @DisplayName("Получение запроса по RegistorId")
    @Test
    void findRequestByIdTest() throws Exception {
        when(itemRequestService.findByRegistorId(1L)).thenReturn(requests);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(requests))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestListAnswerDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestListAnswerDto.getDescription())))
                .andExpect(jsonPath("$[0].requesterName", is(itemRequestListAnswerDto.getRequesterName())))
                .andExpect(jsonPath("$[0].created", is(itemRequestListAnswerDto.getCreated().toString())));
    }

    @DisplayName("Получение всех запросов от всех пользователей")
    @Test
    void findAllRequestTest() throws Exception {
        when(itemRequestService.findAll()).thenReturn(requestsDto);

        mockMvc.perform(get("/requests/all")
                        .content(mapper.writeValueAsString(requestsDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$[0].created", is(requestDto.getCreated().toString())));
    }

    @DisplayName("Получение запроса по id")
    @Test
    void findByRequestIdTest() throws Exception {
        when(itemRequestService.findById(1L)).thenReturn(itemRequestListAnswerDto);

        mockMvc.perform(get("/requests/1")
                        .content(mapper.writeValueAsString(itemRequestListAnswerDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestListAnswerDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestListAnswerDto.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestListAnswerDto.getCreated().toString())));
    }

    @DisplayName("Получение несуществующего запроса")
    @Test
    void findNonExistentRequest() throws Exception {
        long requestId = 999L;
        long userId = 1L;

        when(itemRequestService.findById(requestId))
                .thenThrow(new NotFoundException("ItemRequest not found"));

        mockMvc.perform(get("/requests/{requestsId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("ItemRequest not found")));
    }

    @DisplayName("Получение запроса по id несуществующего пользователя")
    @Test
    void findNonExistentUserRequest() throws Exception {
        long userId = 999L;

        when(itemRequestService.findByRegistorId(userId))
                .thenThrow(new NotFoundException("ItemRequest not found"));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("ItemRequest not found")));
    }
}
