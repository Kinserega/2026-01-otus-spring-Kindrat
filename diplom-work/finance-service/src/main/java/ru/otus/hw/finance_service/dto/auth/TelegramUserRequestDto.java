package ru.otus.hw.finance_service.dto.auth;

public record TelegramUserRequestDto(
        Long telegramId,
        String username,
        String firstName,
        String lastName,
        String locale
) {
}