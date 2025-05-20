package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBooker_id(long bookerId);

    List<Booking> findByItemOwner_id(long ownerId);

    List<Booking> findByItem_idAndStatus(long itemId, Status status);

    Booking findByItem_idAndBooker_id(long itemId, long bookerId);
}
