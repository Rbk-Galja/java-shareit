package ru.practicum.shareit.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class ErrorResponse {
    String error;
    String description;
    List<String> errors;

    public ErrorResponse(String error, String description) {
        this.error = error;
        this.description = description;
    }

    public ErrorResponse(String error, List<String> errors) {
        this.error = error;
        this.errors = errors;
    }
}
