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
public class AddCategoryCallbackHandler implements CallbackHandler {

    private final TelegramUserService telegramUserService;

    private final UserBotStateService userBotStateService;

    private final TelegramMessageService telegramMessageService;

    @Override
    public boolean supports(String callbackData) {
        return callbackData.startsWith(Constants.ADD_CATEGORY_CALLBACK_PREFIX);
    }

    @Override
    public void handle(Update update) {
        FinanceOperationType operationType = extractOperationType(
                update.getCallbackQuery().getData()
        );

        UserResponseDto user = telegramUserService.getOrCreateUser(
                update.getCallbackQuery().getFrom()
        );

        userBotStateService.waitCategoryName(
                user.id(),
                operationType
        );

        SendMessage message = SendMessage.builder()
                .chatId(update.getCallbackQuery().getMessage().getChatId())
                .text("Введите название новой категории:")
                .build();

        telegramMessageService.sendMessage(message);
    }

    private FinanceOperationType extractOperationType(String callbackData) {
        String operationType = callbackData.substring(
                Constants.ADD_CATEGORY_CALLBACK_PREFIX.length()
        );

        return FinanceOperationType.valueOf(operationType);
    }
}