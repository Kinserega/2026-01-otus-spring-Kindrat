package ru.otus.hw.finance_service.telegram.handler.callback;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.otus.hw.finance_service.dto.auth.UserResponseDto;
import ru.otus.hw.finance_service.service.TelegramUserService;
import ru.otus.hw.finance_service.service.UserBotStateService;
import ru.otus.hw.finance_service.telegram.keyboard.Constants;
import ru.otus.hw.finance_service.telegram.service.TelegramMessageService;

@Component
@RequiredArgsConstructor
public class EditOperationCallbackHandler implements CallbackHandler {

    private final TelegramUserService telegramUserService;

    private final UserBotStateService userBotStateService;

    private final TelegramMessageService telegramMessageService;

    @Override
    public boolean supports(String callbackData) {
        return callbackData.startsWith(Constants.EDIT_OPERATION_CALLBACK_PREFIX);
    }

    @Override
    public void handle(Update update) {
        UserResponseDto user = telegramUserService.getOrCreateUser(update.getCallbackQuery().getFrom());

        Long operationId = extractOperationId(update.getCallbackQuery().getData());

        userBotStateService.waitOperationUpdateAmount(user.id(), operationId);

        SendMessage message = SendMessage.builder()
                .chatId(update.getCallbackQuery().getMessage().getChatId())
                .text("Введите новую сумму финансовой операции:")
                .build();

        telegramMessageService.sendMessage(message);
    }

    private Long extractOperationId(String callbackData) {
        String operationId = callbackData.substring(Constants.EDIT_OPERATION_CALLBACK_PREFIX.length());

        return Long.valueOf(operationId);
    }
}