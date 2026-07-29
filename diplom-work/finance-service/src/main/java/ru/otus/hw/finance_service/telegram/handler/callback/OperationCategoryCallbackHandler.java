package ru.otus.hw.finance_service.telegram.handler.callback;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.otus.hw.finance_service.dto.auth.UserResponseDto;
import ru.otus.hw.finance_service.enums.FinanceOperationType;
import ru.otus.hw.finance_service.service.TelegramUserService;
import ru.otus.hw.finance_service.service.UserBotStateService;
import ru.otus.hw.finance_service.telegram.keyboard.Constants;
import ru.otus.hw.finance_service.telegram.service.TelegramMessageService;

@Component
@RequiredArgsConstructor
public class OperationCategoryCallbackHandler implements CallbackHandler {

    private final TelegramUserService telegramUserService;

    private final UserBotStateService userBotStateService;

    private final TelegramMessageService telegramMessageService;


    @Override
    public boolean supports(String callbackData) {
        return callbackData.startsWith(Constants.CATEGORY_CALLBACK_PREFIX);
    }

    @Override
    public void handle(Update update) {
        CallbackData callbackData = parseCallbackData(update.getCallbackQuery().getData());

        UserResponseDto user = telegramUserService.getOrCreateUser(update.getCallbackQuery().getFrom());

        userBotStateService.waitOperationAmount(
                user.id(),
                callbackData.categoryId(),
                callbackData.operationType()
        );

        SendMessage message = SendMessage.builder()
                .chatId(update.getCallbackQuery().getMessage().getChatId())
                .text(buildMessageText(callbackData.operationType()))
                .build();

        telegramMessageService.sendMessage(message);
    }

    private CallbackData parseCallbackData(String callbackData) {
        String value = callbackData.substring(Constants.CATEGORY_CALLBACK_PREFIX.length());
        String[] parts = value.split(":");
        return new CallbackData(FinanceOperationType.valueOf(parts[0]), Long.valueOf(parts[1]));
    }

    private String buildMessageText(FinanceOperationType operationType) {
        return operationType == FinanceOperationType.EXPENSE
                ? "Введите сумму расхода:"
                : "Введите сумму дохода:";
    }

    private record CallbackData(
            FinanceOperationType operationType,
            Long categoryId
    ) {
    }
}