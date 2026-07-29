package ru.otus.hw.auth_service.service;

import ru.otus.hw.auth_service.dto.request.TelegramUserRequestDto;
import ru.otus.hw.auth_service.dto.response.UserResponseDto;

public interface UserService {

    UserResponseDto getOrCreateTelegramUser(TelegramUserRequestDto request);

    UserResponseDto findById(Long userId);

    UserResponseDto findByTelegramId(Long telegramId);
}