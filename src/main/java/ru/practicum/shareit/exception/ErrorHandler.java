package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(DuplicatedDataException.class)
    public ResponseEntity<String> handleConflict(final DuplicatedDataException ex) {
        return response("Указанный email уже используется", ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ParameterNotValidException.class)
    public ResponseEntity<String> handleIncorrectPost(final ParameterNotValidException e) {
        return response("Введите запрос для поиска", e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(final MethodArgumentNotValidException e) {
        return response("Указаны неккоректные данные в полях объекта {}", e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResponse handleServerError(final Throwable e) {
        log.info("500 {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage(), "Ошибка работы сервера");
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ErrorResponse handleNotFound(final NotFoundException e) {
        log.error("Запрашиваемый ресурс не найден");
        return new ErrorResponse(
                "error", e.getMessage()
        );
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler
    public ErrorResponse handleNoAccess(final NoAccessException e) {
        return new ErrorResponse("Отказано в доступе", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResponse handleNoAccessToComment(final NoAccessAddCommentException e) {
        return new ErrorResponse("Отказано в доступе", e.getMessage());
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
