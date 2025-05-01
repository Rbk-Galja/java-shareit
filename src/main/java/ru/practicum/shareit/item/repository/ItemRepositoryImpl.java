package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    long nextId;

    @Override
    public Optional<Item> getById(Long id) {
        return Optional.of(items.get(id));
    }

    @Override
    public Collection<Item> getAll() {
        return items.values();
    }

    @Override
    public Item save(Item item) {
        item.setId(getNextId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Long id, Item item) {
        items.replace(id, item);
        return item;
    }

    @Override
    public List<Item> getUserItems(User user) {
        return getAll().stream()
                .filter(item -> item.getOwner().equals(user))
                .toList();
    }

    @Override
    public List<Item> searchItem(String text) {
        return getAll().stream()
                .filter(item -> item.getAvailable() && item.getName().matches("(?i)" + text)
                        || item.getDescription().matches("(?i)" + text))
                .toList();
    }

    private long getNextId() {
        return ++nextId;
    }
}
