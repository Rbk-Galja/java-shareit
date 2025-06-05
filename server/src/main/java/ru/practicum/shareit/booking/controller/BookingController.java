package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto add(@RequestBody @Valid NewBookingRequest request,
                          @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.add(request, userId);
    }

    @PatchMapping(value = "/{bookingId}", params = {"approved"})
    @ResponseStatus(HttpStatus.OK)
    public BookingDto updateStatus(@RequestHeader("X-Sharer-User-Id") long userId,
                                   @PathVariable("bookingId") long bookingId,
                                   @RequestParam(name = "approved") boolean approved) {
        return bookingService.updateStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto findById(@RequestHeader("X-Sharer-User-Id") long userId,
                               @PathVariable("bookingId") long bookingId) {
        return bookingService.findById(userId, bookingId);
    }


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> findByBookerId(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestParam(defaultValue = "ALL") State state) {
        return bookingService.findByBookerId(userId, state);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> findByOwnerId(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @RequestParam(defaultValue = "ALL") State state) {
        return bookingService.findByOwnerId(userId, state);
    }
}
