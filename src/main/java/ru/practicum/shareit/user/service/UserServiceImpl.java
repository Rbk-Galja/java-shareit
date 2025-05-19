package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserDtoMapper;
import ru.practicum.shareit.user.model.User;

import java.sql.SQLException;
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
        return userRepository.findAll().stream()
                .map(UserDtoMapper::mapToUserDto)
                .toList();
    }

    @Override
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
    public UserDto update(long id, UpdateUserRequest request) {
        log.info("Началось обновление пользователя id {}", id);
        User oldUser = UserDtoMapper.mapToUser(getById(id));
        User updateUser = UserDtoMapper.mapToUserUpdate(oldUser, request);
        try {
            updateUser = userRepository.save(updateUser);
            log.info("Обновление пользователя {} завершено", updateUser);
        } catch (DataIntegrityViolationException e) {
            if (checkEmailException(e)) {
                log.error("Указанный при обновлении email {} уже используется", request.getEmail());
                throw new DuplicatedDataException("Данный email уже используется");
            }
        }
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
    public void deleteUser(long id) {
        log.info("Началось удаление пользователя id = {}", id);
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        log.error("Пользователь не был удален");
        if (user != null) {
            userRepository.delete(user);
            log.info("Удаление пользователя id = {} прошло успешно", id);
        }
    }

    private boolean checkEmailException(DataIntegrityViolationException e) {
        return e.getMostSpecificCause().getClass().getName().equals("org.postgresql.util.PSQLException")
                && ((SQLException) e.getMostSpecificCause()).getSQLState().equals("23505");
    }
}
