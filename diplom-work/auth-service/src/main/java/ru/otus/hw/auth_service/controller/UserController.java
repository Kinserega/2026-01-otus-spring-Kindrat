package ru.otus.hw.auth_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.otus.hw.auth_service.dto.request.TelegramUserRequestDto;
import ru.otus.hw.auth_service.dto.response.UserResponseDto;
import ru.otus.hw.auth_service.service.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public UserResponseDto findById(@PathVariable Long userId) {
        return userService.findById(userId);
    }

    @GetMapping("/by-telegram/{telegramId}")
    public UserResponseDto findByTelegramId(@PathVariable Long telegramId) {
        return userService.findByTelegramId(telegramId);
    }

    @PostMapping("/telegram")
    public UserResponseDto getOrCreateTelegramUser(@Valid @RequestBody TelegramUserRequestDto request) {
        return userService.getOrCreateTelegramUser(request);
    }
}