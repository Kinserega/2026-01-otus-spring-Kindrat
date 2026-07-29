package ru.otus.hw.auth_service.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TelegramUserRequestDto(
        @NotNull(message = "Идентификатор Telegram обязателен")
        Long telegramId,

        @Size(max = 100, message = "Логин Telegram не должен превышать 100 символов")
        String username,

        @Size(max = 100, message = "Имя пользователя не должно превышать 100 символов")
        String firstName,

        @Size(max = 100, message = "Фамилия пользователя не должна превышать 100 символов")
        String lastName,

        String locale
) {
}