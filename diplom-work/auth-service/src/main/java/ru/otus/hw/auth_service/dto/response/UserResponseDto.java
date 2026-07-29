package ru.otus.hw.auth_service.dto.response;

public record UserResponseDto(
        Long id,
        String username,
        String firstName,
        String lastName,
        Long telegramId,
        boolean enabled
) {
}