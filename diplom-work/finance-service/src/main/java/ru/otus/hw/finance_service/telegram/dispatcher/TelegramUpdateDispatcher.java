package ru.otus.hw.finance_service.telegram.dispatcher;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.otus.hw.finance_service.dto.auth.UserResponseDto;
import ru.otus.hw.finance_service.entity.UserBotState;
import ru.otus.hw.finance_service.service.TelegramUserService;
import ru.otus.hw.finance_service.service.UserBotStateService;
import ru.otus.hw.finance_service.telegram.handler.TelegramUpdateExceptionHandler;
import ru.otus.hw.finance_service.telegram.handler.callback.CallbackHandler;
import ru.otus.hw.finance_service.telegram.handler.command.CommandHandler;
import ru.otus.hw.finance_service.telegram.handler.message.MessageHandler;
import ru.otus.hw.finance_service.telegram.handler.state.StateHandler;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TelegramUpdateDispatcher {

    private final List<CommandHandler> commandHandlers;

    private final List<StateHandler> stateHandlers;

    private final List<CallbackHandler> callbackHandlers;

    private final List<MessageHandler> messageHandlers;

    private final TelegramUserService telegramUserService;

    private final UserBotStateService userBotStateService;

    private final TelegramUpdateExceptionHandler telegramUpdateExceptionHandler;

    public void dispatch(Update update) {
        try {
            dispatchSafely(update);
        } catch (RuntimeException exception) {
            telegramUpdateExceptionHandler.handle(update, exception);
        }
    }

    private void dispatchSafely(Update update) {
        if (update.hasCallbackQuery()) {
            dispatchCallback(update);
            return;
        }
        if (!isTextMessage(update)) {
            return;
        }
        dispatchTextMessage(update);
    }

    private void dispatchTextMessage(Update update) {
        String messageText = update.getMessage().getText();

        if (isCommand(messageText)) {
            dispatchCommand(update, messageText);
            return;
        }
        if (dispatchMessage(update, messageText)) {
            return;
        }

        dispatchState(update);

    }

    private void dispatchCommand(Update update, String command) {
        commandHandlers.stream()
                .filter(handler -> handler.supports(command))
                .findFirst()
                .ifPresent(handler -> handler.handle(update));
    }

    private boolean dispatchState(Update update) {
        UserResponseDto user = telegramUserService.getOrCreateUser(update.getMessage().getFrom());
        Optional<UserBotState> userBotState = userBotStateService.findByUserId(user.id());
        return userBotState.map(botState -> stateHandlers.stream()
                .filter(handler -> handler.supports(botState))
                .findFirst()
                .map(handler -> {
                    handler.handle(update, botState);
                    return true;
                })
                .orElse(false)).orElse(false);

    }

    private void dispatchCallback(Update update) {
        String callbackData = update.getCallbackQuery().getData();

        callbackHandlers.stream()
                .filter(handler -> handler.supports(callbackData))
                .findFirst()
                .ifPresent(handler -> handler.handle(update));
    }

    private boolean dispatchMessage(Update update, String messageText) {
        return messageHandlers.stream()
                .filter(handler -> handler.supports(messageText))
                .findFirst()
                .map(handler -> {
                    resetUserState(update);
                    handler.handle(update);
                    return true;
                })
                .orElse(false);
    }

    private void resetUserState(Update update) {
        UserResponseDto user = telegramUserService.getOrCreateUser(update.getMessage().getFrom());
        userBotStateService.reset(user.id());
    }
    private boolean isTextMessage(Update update) {
        return update.hasMessage() && update.getMessage().hasText();
    }

    private boolean isCommand(String messageText) {
        return messageText.startsWith("/");
    }
}