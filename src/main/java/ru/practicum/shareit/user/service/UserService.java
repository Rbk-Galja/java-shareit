package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAll();

    UserDto add(NewUserRequest request);

    UserDto update(long id, UpdateUserRequest request);

    UserDto getById(long id);

    boolean deleteUser(long id);
}
