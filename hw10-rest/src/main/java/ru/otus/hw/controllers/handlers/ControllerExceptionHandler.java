package ru.otus.hw.controllers.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.otus.hw.dto.ErrorResponse;
import ru.otus.hw.exceptions.EntityNotFoundException;

@RestControllerAdvice
public class ControllerExceptionHandler {


    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(RuntimeException exception) {
        ErrorResponse errorResponse = new ErrorResponse(
                exception.getMessage(),
                exception.getClass().getSimpleName(),
                HttpStatus.NOT_FOUND.value()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}