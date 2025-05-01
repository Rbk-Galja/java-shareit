package ru.practicum.shareit.exception;

public class DuplicatedDataException extends IllegalArgumentException {
    public DuplicatedDataException(String message) {
        super(message);
    }
}
