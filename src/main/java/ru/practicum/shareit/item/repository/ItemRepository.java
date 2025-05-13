package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Optional<Item> getById(Long id);

    Collection<Item> getAll();

    Item save(Item item);

    Item update(Long id, Item item);

    List<Item> getUserItems(User user);

    List<Item> searchItem(String text);
}
