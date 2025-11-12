package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    ItemDtoBooking getById(long itemId);

    ItemDto add(long userId, NewItemRequest request);

    ItemDto update(long userId, long id, UpdateItemRequest request);

    List<ItemDto> getUserItems(long userId);

    List<ItemDto> searchItems(String text);

    CommentDto addComment(NewCommentRequest request, long itemId, long userId);
}
