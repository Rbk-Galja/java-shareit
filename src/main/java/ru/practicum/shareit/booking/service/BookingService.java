package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;

import java.util.List;

public interface BookingService {

    List<BookingDto> findAll();

    BookingDto add(NewBookingRequest request, long itemId);

    BookingDto updateStatus(long userId, long id, boolean approved);

    BookingDto findById(long userId, long bookingId);

    List<BookingDto> findByBookerId(long bookerId, String state);

    List<BookingDto> findByOwnerId(long ownerId, String state);
}
