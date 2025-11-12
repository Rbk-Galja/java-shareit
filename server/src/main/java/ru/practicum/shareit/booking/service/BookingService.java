package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {

    BookingDto add(NewBookingRequest request, long itemId);

    BookingDto updateStatus(long userId, long id, boolean approved);

    BookingDto findById(long userId, long bookingId);

    List<BookingDto> findByBookerId(long bookerId, State state);

    List<BookingDto> findByOwnerId(long ownerId, State state);
}
