package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.dto.State;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
@Slf4j
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestBody @Valid NewBookingRequest request,
                                      @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Creating booking {}, userId={}", request, userId);
        return bookingClient.add(userId, request);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateStatus(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @PathVariable("bookingId") long bookingId,
                                               @RequestParam(name = "approved") boolean approved) {
        log.info("Начинаем изменение статуса бронирования id = {} пользователем id = {}", bookingId, userId);
        return bookingClient.updateStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findById(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @PathVariable("bookingId") long bookingId) {
        log.info("Возвращаем бронирование id = {} для пользователя id = {}", bookingId, userId);
        return bookingClient.findById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findByBookerId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam(defaultValue = "ALL") State state) {
        log.info("Возвращаем все бронирования для пользователя id = {} по запросу {}", userId, state);
        return bookingClient.findByBookerId(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findByOwnerId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestParam(name = "state", defaultValue = "ALL") State state) {
        log.info("Возвращаем бронирования для всех вещей пользователя id = {} по запросу {}", userId, state);
        return bookingClient.findByOwnerId(userId, state);
    }
}