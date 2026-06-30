package ru.otus.hw.dto;

public record ErrorResponse(
        String message,
        String error,
        int status
) {
}
