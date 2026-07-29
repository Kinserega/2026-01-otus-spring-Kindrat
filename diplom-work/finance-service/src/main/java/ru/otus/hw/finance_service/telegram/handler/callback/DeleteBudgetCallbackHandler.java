package ru.otus.hw.finance_service.telegram.handler.callback;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.otus.hw.finance_service.dto.auth.UserResponseDto;
import ru.otus.hw.finance_service.service.BudgetService;
import ru.otus.hw.finance_service.service.TelegramUserService;
import ru.otus.hw.finance_service.telegram.keyboard.Constants;
import ru.otus.hw.finance_service.telegram.keyboard.MainMenuKeyboardFactory;
import ru.otus.hw.finance_service.telegram.service.TelegramMessageService;

@Component
@RequiredArgsConstructor
public class DeleteBudgetCallbackHandler implements CallbackHandler {

    private final TelegramUserService telegramUserService;

    private final BudgetService budgetService;

    private final MainMenuKeyboardFactory mainMenuKeyboardFactory;

    private final TelegramMessageService telegramMessageService;

    @Override
    public boolean supports(String callbackData) {
        return callbackData.startsWith(
                Constants.DELETE_BUDGET_CALLBACK_PREFIX
        );
    }

    @Override
    public void handle(Update update) {
        UserResponseDto user = telegramUserService.getOrCreateUser(
                update.getCallbackQuery().getFrom()
        );

        Long budgetId = extractBudgetId(
                update.getCallbackQuery().getData()
        );

        budgetService.delete(
                user.id(),
                budgetId
        );

        SendMessage message = SendMessage.builder()
                .chatId(update.getCallbackQuery().getMessage().getChatId())
                .text("🗑 Бюджет успешно удалён.")
                .replyMarkup(mainMenuKeyboardFactory.create())
                .build();

        telegramMessageService.sendMessage(message);
    }

    private Long extractBudgetId(String callbackData) {
        return Long.valueOf(
                callbackData.substring(
                        Constants.DELETE_BUDGET_CALLBACK_PREFIX.length()
                )
        );
    }
}