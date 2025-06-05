package ru.practicum.shareit.request.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestListAnswerDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @RequestBody @Valid NewRequest request) {
        return itemRequestService.add(userId, request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestListAnswerDto> findByRegistorId(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.findByRegistorId(userId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDto> findAll() {
        return itemRequestService.findAll();
    }

    @GetMapping("{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestListAnswerDto findById(@PathVariable("requestId") long requestId) {
        return itemRequestService.findById(requestId);
    }
}
