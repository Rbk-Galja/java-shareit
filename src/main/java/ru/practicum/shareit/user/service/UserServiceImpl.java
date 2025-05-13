package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserDtoMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDto> getAll() {
        log.info("Начинаем получение всех пользователей");
        return userRepository.getAll().stream()
                .map(UserDtoMapper::mapToUserDto)
                .toList();
    }

    @Override
    public UserDto add(NewUserRequest request) {
        log.info("Началось создание пользователя {}", request);
        if (userRepository.checkEmail(request.getEmail())) {
            log.error("Указанный при добавлении email {} уже используется", request.getEmail());
            throw new DuplicatedDataException("Данный email уже используется");
        }
        User user = UserDtoMapper.mapToUserAdd(request);
        user = userRepository.save(user);
        log.info("Пользователю присвоен id {}", user.getId());

        UserDto userDto = UserDtoMapper.mapToUserDto(user);
        log.info("Создание пользователя {} завершено", userDto);

        return userDto;
    }

    @Override
    public UserDto update(long id, UpdateUserRequest request) {
        log.info("Началось обновление пользователя id {}", id);
        if (userRepository.checkEmail(request.getEmail())) {
            log.error("Указанный при обновлении email {} уже используется", request.getEmail());
            throw new DuplicatedDataException("Данный email уже используется");
        }
        User oldUser = UserDtoMapper.mapToUser(getById(id));
        User updateUser = UserDtoMapper.mapToUserUpdate(oldUser, request);
        updateUser = userRepository.update(id, updateUser);
        log.info("Обновление пользователя {} завершено", updateUser);
        return UserDtoMapper.mapToUserDto(updateUser);
    }

    @Override
    public UserDto getById(long id) {
        log.info("Получаем пользователя с id {}", id);
        User user = userRepository.getById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        log.info("Возвращаем пользователя {}", user);
        return UserDtoMapper.mapToUserDto(user);
    }

    @Override
    public boolean deleteUser(long id) {
        log.info("Началось удаление пользователя id = {}", id);
        if (userRepository.deleteUser(id)) {
            log.info("Удаление пользователя id = {} прошло успешно", id);
            return true;
        }
        log.error("Пользователь не был удален");
        throw new NotFoundException("Пользователь не найден");
    }
}
