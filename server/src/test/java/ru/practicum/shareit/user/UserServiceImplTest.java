package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    final Long userId = 1L;
    final User user = User.builder()
            .id(userId)
            .name("User 1")
            .email("user1@example.com")
            .build();
    final NewUserRequest request = NewUserRequest.builder().name("User 1")
            .email("user1@example.com").build();
    final UserDto userDto = UserDto.builder()
            .id(1L)
            .name("Иван Иванов")
            .email("ivan@example.com")
            .build();
    UpdateUserRequest requestUpdate = UpdateUserRequest.builder()
            .name("Updated Name")
            .email("updated@email.com")
            .build();

    @DisplayName("Получение всех пользователей")
    @Test
    void testGetAllSuccess() {
        User user2 = User.builder()
                .id(2L)
                .name("User 2")
                .email("user2@example.com")
                .build();

        when(userRepository.findAll()).thenReturn(List.of(user, user2));
        List<UserDto> result = userService.getAll();

        assertEquals(2, result.size());
        assertEquals(UserDtoMapper.mapToUserDto(user), result.get(0));
        assertEquals(UserDtoMapper.mapToUserDto(user2), result.get(1));
        verify(userRepository).findAll();
    }

    @DisplayName("Получение пустого листа пользователей")
    @Test
    void testGetAllEmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserDto> result = userService.getAll();

        assertTrue(result.isEmpty());
        verify(userRepository).findAll();
    }

    @DisplayName("Получение валидного пользователя")
    @Test
    void getUserById() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto result = userService.getById(userId);

        assertEquals(user.getName(), result.getName());
        verify(userRepository).findById(userId);
    }

    @DisplayName("Ошибка получения несуществующего пользователя")
    @Test
    void testGetByIdUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getById(userId));
    }

    @Test
    @DisplayName("Успешное добавление пользователя")
    void testAddUserSuccess() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.add(request);

        assertNotNull(result.getId());
        assertEquals(request.getName(), result.getName());
        assertEquals(request.getEmail(), result.getEmail());

        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Добавление пользователя с существующим email")
    void testAddUser_DuplicateEmail() {
        when(userRepository.save(any(User.class)))
                .thenThrow(new DuplicatedDataException("Пользователь с таким email уже существует"));

        assertThrows(DuplicatedDataException.class, () -> userService.add(request));

        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Добавление пользователя с пустыми данными")
    void testAddUser_EmptyData() {
        NewUserRequest request = NewUserRequest.builder()
                .name("")
                .email("")
                .build();

        assertThrows(NullPointerException.class, () -> userService.add(request));
    }

    @DisplayName("Обновление валидного пользователя")
    @Test
    void testUpdateSuccess() {
        User updatedUser = User.builder()
                .id(userId)
                .name("Updated Name")
                .email("updated@email.com")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDto result = userService.update(userId, requestUpdate);

        assertEquals("Updated Name", result.getName());
        assertEquals("updated@email.com", result.getEmail());
        verify(userRepository).save(any(User.class));
        verify(userRepository).findById(userId);
    }

    @DisplayName("Обновление несуществующего пользователя")
    @Test
    void testUpdateNotFound() {
        long userId = 2L;
        UpdateUserRequest request = UpdateUserRequest.builder()
                .name("Updated Name")
                .email("updated@email.com")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.update(userId, request));
    }

    @DisplayName("Обновление с дубликатом email")
    @Test
    void testUpdateDuplicatedEmail() {
        User oldUser = User.builder()
                .id(userId)
                .name("Old Name")
                .email("old@email.com")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));
        when(userRepository.save(any(User.class)))
                .thenThrow(new DuplicatedDataException("Email already exists"));

        assertThrows(DuplicatedDataException.class, () -> userService.update(userId, requestUpdate));
    }

    @DisplayName("Успешное удаление пользователя")
    @Test
    void testDeleteUserSuccess() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUser(userId);

        verify(userRepository).findById(userId);
        verify(userRepository).delete(user);
    }

    @DisplayName("Удаление несуществуюшего пользователя")
    @Test
    void testDeleteUserInvalid() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.deleteUser(userId));
    }
}




