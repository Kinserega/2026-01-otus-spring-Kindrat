package ru.otus.hw.finance_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.otus.hw.finance_service.dto.auth.TelegramUserRequestDto;
import ru.otus.hw.finance_service.dto.auth.UserResponseDto;

@FeignClient(
        name = "auth-service",
        url = "${integration.auth-service.url}"
)
public interface AuthServiceClient {

    @PostMapping("/api/users/telegram")
    UserResponseDto getOrCreateTelegramUser(@RequestBody TelegramUserRequestDto request);
}