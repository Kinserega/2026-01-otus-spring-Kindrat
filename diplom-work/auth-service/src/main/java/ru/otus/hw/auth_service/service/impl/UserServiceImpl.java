package ru.otus.hw.auth_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.auth_service.dto.request.TelegramUserRequestDto;
import ru.otus.hw.auth_service.dto.response.UserResponseDto;
import ru.otus.hw.auth_service.entity.User;
import ru.otus.hw.auth_service.exception.UserNotFoundException;
import ru.otus.hw.auth_service.mapper.UserMapper;
import ru.otus.hw.auth_service.repository.UserRepository;
import ru.otus.hw.auth_service.service.UserService;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponseDto getOrCreateTelegramUser(TelegramUserRequestDto request) {
        return userRepository.findByTelegramId(request.telegramId())
                .map(userMapper::toResponseDto)
                .orElseGet(() -> createTelegramUser(request));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto findById(Long userId) {
        return userRepository.findById(userId)
                .map(userMapper::toResponseDto)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID %d не найден".formatted(userId)));
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserResponseDto findByTelegramId(Long telegramId) {
        return userRepository.findByTelegramId(telegramId)
                .map(userMapper::toResponseDto)
                .orElseThrow(() -> new UserNotFoundException(
                        "Пользователь с Telegram ID %d не найден".formatted(telegramId)
                ));
    }

    private UserResponseDto createTelegramUser(TelegramUserRequestDto request) {
        User user = new User();
        user.setUsername(buildTelegramUsername(request));
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setTelegramId(request.telegramId());
        user.setEnabled(true);
        User savedUser = userRepository.save(user);
        return userMapper.toResponseDto(savedUser);
    }

    private String buildTelegramUsername(TelegramUserRequestDto request) {
        if (request.username() != null && !request.username().isBlank()) {
            return "tg_" + request.username();
        }
        return "tg_" + request.telegramId();
    }
}