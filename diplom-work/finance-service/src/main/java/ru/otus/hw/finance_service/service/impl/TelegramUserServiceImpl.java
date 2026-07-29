package ru.otus.hw.finance_service.service.impl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.otus.hw.finance_service.client.AuthServiceClient;
import ru.otus.hw.finance_service.dto.auth.TelegramUserRequestDto;
import ru.otus.hw.finance_service.dto.auth.UserResponseDto;
import ru.otus.hw.finance_service.exception.ExternalServiceUnavailableException;
import ru.otus.hw.finance_service.service.TelegramUserService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TelegramUserServiceImpl implements TelegramUserService {

    private static final String DEFAULT_LANGUAGE_CODE = "ru";

    private final AuthServiceClient authServiceClient;

    @Override
    @CircuitBreaker(name = "authServiceBreaker", fallbackMethod = "getOrCreateUserFallback")
    public UserResponseDto getOrCreateUser(User telegramUser) {
        String languageCode = Optional.ofNullable(telegramUser.getLanguageCode())
                .filter(code -> !code.isBlank())
                .orElse(DEFAULT_LANGUAGE_CODE);
        TelegramUserRequestDto request = new TelegramUserRequestDto(
                telegramUser.getId(),
                telegramUser.getUserName(),
                telegramUser.getFirstName(),
                telegramUser.getLastName(),
                languageCode
        );

        return authServiceClient.getOrCreateTelegramUser(request);
    }

    private UserResponseDto getOrCreateUserFallback(User telegramUser, Throwable throwable) {
        throw new ExternalServiceUnavailableException("Сервис пользователей временно недоступен", throwable);
    }
}