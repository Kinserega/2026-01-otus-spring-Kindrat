package ru.otus.hw.auth_service.dto.response;

import java.time.LocalDateTime;

public record ErrorResponseDto(
        String message,
        String path,
        LocalDateTime timestamp
) {
}