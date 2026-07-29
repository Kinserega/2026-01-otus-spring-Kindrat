package ru.otus.hw.finance_service.dto.auth;

import java.util.Set;

public record UserResponseDto(
        Long id,
        String username,
        String firstName,
        String lastName,
        Long telegramId,
        boolean enabled,
        Set<String> roles
) {
}