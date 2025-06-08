package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestListAnswerDto;
import ru.practicum.shareit.request.dto.NewRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto add(long userId, NewRequest request);

    List<ItemRequestListAnswerDto> findByRegistorId(long requestorId);

    List<ItemRequestDto> findAll();

    ItemRequestListAnswerDto findById(long requestId);
}
