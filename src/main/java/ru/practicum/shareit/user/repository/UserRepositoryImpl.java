package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private long nextId;

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public User save(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(Long id, User user) {
        users.replace(id, user);
        return user;
    }

    @Override
    public boolean checkEmail(String email) {
        return !users.values().stream()
                .filter(user -> user.getEmail().equals(email)).toList().isEmpty();
    }

    @Override
    public Optional<User> getById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public boolean deleteUser(Long id) {
        return users.remove(id) != null;
    }

    private long getNextId() {
        return ++nextId;
    }
}
