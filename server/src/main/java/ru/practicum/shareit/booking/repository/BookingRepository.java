package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBooker_id(long bookerId);

    List<Booking> findByItemOwner_id(long ownerId);

    List<Booking> findByItem_idAndStatus(long itemId, Status status);

    Booking findByItemAndBooker(Item item, User booker);
}
