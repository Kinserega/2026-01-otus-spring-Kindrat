package ru.otus.hw.finance_service.telegram.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.otus.hw.finance_service.dto.auth.UserResponseDto;
import ru.otus.hw.finance_service.exception.DocumentGenerationException;
import ru.otus.hw.finance_service.exception.ExternalServiceUnavailableException;
import ru.otus.hw.finance_service.service.TelegramUserService;
import ru.otus.hw.finance_service.service.UserBotStateService;
import ru.otus.hw.finance_service.telegram.keyboard.MainMenuKeyboardFactory;
import ru.otus.hw.finance_service.telegram.service.TelegramMessageService;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramUpdateExceptionHandler {

    private final TelegramUserService telegramUserService;

    private final UserBotStateService userBotStateService;

    private final MainMenuKeyboardFactory mainMenuKeyboardFactory;

    private final TelegramMessageService telegramMessageService;

    public void handle(Update update, RuntimeException exception) {
        resetUserState(update);
        sendErrorMessage(resolveChatId(update), resolveErrorMessage(exception));
    }

    private void resetUserState(Update update) {
        User telegramUser = resolveTelegramUser(update);

        if (telegramUser == null) {
            return;
        }
        try {
            UserResponseDto user = telegramUserService.getOrCreateUser(telegramUser);
            userBotStateService.reset(user.id());
        } catch (RuntimeException exception) {
            log.error("Не удалось сбросить состояние Telegram-пользователя", exception);
        }
    }

    private void sendErrorMessage(Long chatId, String errorMessage) {
        if (chatId == null) {
            return;
        }

        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("""
                        ⚠️ Не удалось выполнить операцию.
                        %s
                        Выберите другое действие.
                        """.formatted(errorMessage))
                .replyMarkup(mainMenuKeyboardFactory.create())
                .build();

        telegramMessageService.sendMessage(message);
    }

    private String resolveErrorMessage(RuntimeException exception) {
        if (exception instanceof IllegalArgumentException
                || exception instanceof DocumentGenerationException
                || exception instanceof ExternalServiceUnavailableException) {
            return exception.getMessage();
        }
        return "Произошла внутренняя ошибка. Попробуйте повторить действие.";
    }

    private User resolveTelegramUser(Update update) {
        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getFrom();
        }
        if (update.hasMessage()) {
            return update.getMessage().getFrom();
        }

        return null;
    }

    private Long resolveChatId(Update update) {
        if (update.hasCallbackQuery()
                && update.getCallbackQuery().getMessage() != null) {
            return update.getCallbackQuery().getMessage().getChatId();
        }
        if (update.hasMessage()) {
            return update.getMessage().getChatId();
        }

        return null;
    }
}