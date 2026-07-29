package ru.otus.hw.template_service.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.otus.hw.template_service.dto.ErrorResponseDto;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({TemplateLoadingException.class,
                       TemplateRenderingException.class})
    public ResponseEntity<ErrorResponseDto> handleTemplateProcessingException(
            RuntimeException exception,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                exception.getMessage(),
                request,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(
            IllegalArgumentException exception,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                exception.getMessage(),
                request,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> "%s: %s".formatted(
                        error.getField(),
                        error.getDefaultMessage()
                ))
                .orElse("Ошибка валидации запроса");

        return buildErrorResponse(
                message,
                request,
                HttpStatus.BAD_REQUEST
        );
    }

    private ResponseEntity<ErrorResponseDto> buildErrorResponse(
            String message,
            HttpServletRequest request,
            HttpStatus status
    ) {
        ErrorResponseDto response = new ErrorResponseDto(
                message,
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(status).body(response);
    }
}