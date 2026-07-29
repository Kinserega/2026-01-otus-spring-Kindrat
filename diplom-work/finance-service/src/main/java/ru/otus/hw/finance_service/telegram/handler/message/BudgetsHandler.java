package ru.otus.hw.finance_service.telegram.handler.message;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.otus.hw.finance_service.telegram.keyboard.BudgetKeyboardFactory;
import ru.otus.hw.finance_service.telegram.keyboard.Constants;
import ru.otus.hw.finance_service.telegram.service.TelegramMessageService;

@Component
@RequiredArgsConstructor
public class BudgetsHandler implements MessageHandler {

    private final BudgetKeyboardFactory budgetKeyboardFactory;

    private final TelegramMessageService telegramMessageService;

    @Override
    public boolean supports(String messageText) {
        return Constants.BUDGETS_BUTTON.equals(messageText);
    }

    @Override
    public void handle(Update update) {
        SendMessage message = SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text("Выберите действие с бюджетами:")
                .replyMarkup(budgetKeyboardFactory.createManagementMenu())
                .build();

        telegramMessageService.sendMessage(message);
    }
}