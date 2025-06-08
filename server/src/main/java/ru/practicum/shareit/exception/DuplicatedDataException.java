package ru.practicum.shareit.exception;

import org.springframework.dao.DataIntegrityViolationException;

public class DuplicatedDataException extends DataIntegrityViolationException {
    public DuplicatedDataException(String message) {
        super(message);
    }

}
