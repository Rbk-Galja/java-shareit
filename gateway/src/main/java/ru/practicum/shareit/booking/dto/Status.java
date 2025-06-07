package ru.practicum.shareit.booking.dto;

public enum Status {
    WAITING,

    //бронирование подтверждено владельцем
    APPROVED,

    // бронирование отклонено владельцем
    REJECTED,

    //бронирование отменено создателем
    CANCELED
}
