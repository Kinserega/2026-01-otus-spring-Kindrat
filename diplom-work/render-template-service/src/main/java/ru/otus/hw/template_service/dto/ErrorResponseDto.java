package ru.otus.hw.template_service.dto;

import java.time.LocalDateTime;

public record ErrorResponseDto(
        String message,
        String path,
        LocalDateTime timestamp
) {
}