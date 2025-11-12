package ru.practicum.shareit.request.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.NewRequest;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestBody @Valid NewRequest request) {
        log.info("Добавляем запрос {}", request);
        return itemRequestClient.add(userId, request);
    }

    @GetMapping
    public ResponseEntity<Object> findByRegistorId(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получаем все запросы пользователя id = {}", userId);
        return itemRequestClient.findByRegistorId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAll() {
        log.info("Получаем список всех запросов от всех пользователей");
        return itemRequestClient.findAll();
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> findById(@PathVariable("requestId") long requestId) {
        log.info("Получаем запрос id = {}", requestId);
        return itemRequestClient.findById(requestId);
    }
}
