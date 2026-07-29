package ru.otus.hw.finance_service.service;

import org.telegram.telegrambots.meta.api.objects.User;
import ru.otus.hw.finance_service.dto.auth.UserResponseDto;

public interface TelegramUserService {

    UserResponseDto getOrCreateUser(User telegramUser);
}