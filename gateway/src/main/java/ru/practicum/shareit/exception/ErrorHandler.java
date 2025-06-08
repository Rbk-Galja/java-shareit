package ru.practicum.shareit.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    //ошибки валидации данных
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException e) {
        return response("Поля не должны быть пустыми", e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    //ошибки валидации Lombook
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleNotValid(final ConstraintViolationException ex) {
        return response("Указаны некорректные данные", ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResponse handleServerError(final Throwable e) {
        log.info("500 {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage(), "Ошибка работы сервера");
    }

    private static String createJson(String message, String reason) {
        return "{\"error\" : \"" + message + "\"," +
                "\"reason\" : \"" + reason + "\"}";
    }

    private static ResponseEntity<String> response(String message,
                                                   String reason,
                                                   HttpStatus httpStatus) {
        String json = createJson(message, reason);
        return new ResponseEntity<>(json, httpStatus);
    }
}
