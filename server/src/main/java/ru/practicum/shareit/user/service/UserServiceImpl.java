package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
@Setter
public class UserServiceImpl implements UserService {
    UserRepository userRepository;

    @Override
    public List<UserDto> getAll() {
        log.info("Начинаем получение всех пользователей");
        return userRepository.findAll().stream()
                .map(UserDtoMapper::mapToUserDto)
                .toList();
    }

    @Override
    @Transactional(rollbackFor = DuplicatedDataException.class)
    public UserDto add(NewUserRequest request) throws DuplicatedDataException {
        log.info("Началось создание пользователя {}", request);
        User user = UserDtoMapper.mapToUserAdd(request);
        user = userRepository.save(user);
        log.info("Пользователю присвоен id {}", user.getId());
        UserDto userDto = UserDtoMapper.mapToUserDto(user);
        log.info("Создание пользователя {} завершено", userDto);
        return userDto;
    }

    @Override
    @Transactional(rollbackFor = DuplicatedDataException.class)
    public UserDto update(long id, UpdateUserRequest request) throws DuplicatedDataException {
        log.info("Началось обновление пользователя id {}", id);
        User oldUser = UserDtoMapper.mapToUser(getById(id));
        User updateUser = UserDtoMapper.mapToUserUpdate(oldUser, request);
        updateUser = userRepository.save(updateUser);
        log.info("Обновление пользователя {} завершено", updateUser);
        return UserDtoMapper.mapToUserDto(updateUser);
    }

    @Override
    public UserDto getById(long id) {
        log.info("Получаем пользователя с id {}", id);
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        log.info("Возвращаем пользователя {}", user);
        return UserDtoMapper.mapToUserDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(long id) {
        log.info("Началось удаление пользователя id = {}", id);
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        log.error("Пользователь не был удален");
        if (user != null) {
            userRepository.delete(user);
            log.info("Удаление пользователя id = {} прошло успешно", id);
        }
    }
}
