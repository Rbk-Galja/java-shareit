package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.List;

public interface ItemService {
    ItemDto getById(long id);

    List<ItemDto> getAll();

    ItemDto add(long userId, NewItemRequest request);

    ItemDto update(long userId, long id, UpdateItemRequest request);

    List<ItemDto> getUserItems(long userId);

    List<ItemDto> searchItems(String text);
}
