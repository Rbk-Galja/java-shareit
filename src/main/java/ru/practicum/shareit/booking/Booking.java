package ru.practicum.shareit.booking;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validator.UpdateValidate;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@Builder
public class Booking {
    @NotNull(groups = {UpdateValidate.class})
    Long id;

    LocalDateTime start;
    LocalDateTime end;
    Item item;
    User booker;
    Status status;
}
