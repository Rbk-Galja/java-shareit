package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@WebMvcTest(controllers = UserController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserControllerTest {

    @MockBean
    UserService userService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    UserDto userDto;
    NewUserRequest request;
    List<UserDto> users;

    @BeforeEach
    void beforeEach() {
        userDto = UserDto.builder()
                .id(1L)
                .name("name")
                .email("email@mail.ru")
                .build();

        UserDto user2 = UserDto.builder()
                .id(2L)
                .name("name2")
                .email("email2@mail.ru")
                .build();

        users = List.of(userDto, user2);

        request = NewUserRequest.builder().name("name").email("email@mail.ru").build();
    }

    @DisplayName("Получение всех пользователей")
    @Test
    void getAllTest() throws Exception {
        when(userService.getAll()).thenReturn(users);

        mockMvc.perform(get("/users")
                        .content(mapper.writeValueAsString(users))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("name")))
                .andExpect(jsonPath("$[0].email", is("email@mail.ru")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("name2")))
                .andExpect(jsonPath("$[1].email", is("email2@mail.ru")));
    }

    @DisplayName("Получение пользователя по id")
    @Test
    void getByIdTest() throws Exception {
        when(userService.getById(any(Long.class))).thenReturn(userDto);

        mockMvc.perform(get("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @DisplayName("Создание пользователя")
    @Test
    void addUserTest() throws Exception {
        when(userService.add(request)).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @DisplayName("Обновление пользователя")
    @Test
    void updateUserTest() throws Exception {
        UserDto updateUser = UserDto.builder()
                .id(userDto.getId())
                .name("newName")
                .email("newmail@mail.ru")
                .build();

        when(userService.update(any(Long.class), any(UpdateUserRequest.class))).thenReturn(updateUser);

        mockMvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(updateUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updateUser.getName())))
                .andExpect(jsonPath("$.email", is(updateUser.getEmail())));
    }

    @DisplayName("Удаление пользователя")
    @Test
    void deleteUserTest() throws Exception {
        doNothing().when(userService).deleteUser(any(Long.class));

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }

    @DisplayName("Получение несуществующего пользователя")
    @Test
    void findNonExistentUser() throws Exception {
        long userId = 999L;

        when(userService.getById(userId))
                .thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(get("/users/{userId}", userId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("User not found")));
    }
}
