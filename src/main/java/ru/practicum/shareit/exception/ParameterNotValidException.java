package ru.practicum.shareit.exception;

public class ParameterNotValidException extends IllegalArgumentException {
    public ParameterNotValidException(String message) {
        super(message);
    }
}
