package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    Collection<User> getAll();

    User save(User user);

    User update(Long id, User user);

    boolean checkEmail(String email);

    Optional<User> getById(Long id);

    boolean deleteUser(Long id);
}
