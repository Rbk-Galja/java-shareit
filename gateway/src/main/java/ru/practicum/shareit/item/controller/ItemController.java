package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable("itemId") long itemId,
                                             @RequestBody @Valid NewCommentRequest request) {
        log.info("Добавляем комментарий от пользователя id = {} для предмета id = {}", userId, itemId);
        return itemClient.addComment(userId, itemId, request);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("получаем все вещи для пользователя id = {}", userId);
        return itemClient.getUserItems(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@PathVariable("itemId") long itemId) {
        log.info("Получаем предмет id = {}", itemId);
        return itemClient.getById(itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestParam String text) {
        log.info("Начинаем поиск предметов по запросу {}", text);
        return itemClient.searchItem(text);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestBody @Valid NewItemRequest request) {
        log.info("Начинаем создание предмета {} пользователем id = {}", request, userId);
        return itemClient.createItem(userId, request);
    }

    @PatchMapping
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable("itemId") long itemId,
                                             @RequestBody @Valid UpdateItemRequest request) {
        log.info("Начинаем обновление премета id = {} пользователем id = {}", itemId, userId);
        return itemClient.updateItem(userId, itemId, request);
    }
}
