package ru.otus.hw.finance_service.telegram.handler.state;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.otus.hw.finance_service.dto.budget.BudgetResponseDto;
import ru.otus.hw.finance_service.entity.UserBotState;
import ru.otus.hw.finance_service.enums.BotState;
import ru.otus.hw.finance_service.service.BudgetService;
import ru.otus.hw.finance_service.service.UserBotStateService;
import ru.otus.hw.finance_service.telegram.keyboard.MainMenuKeyboardFactory;
import ru.otus.hw.finance_service.telegram.service.TelegramMessageService;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class BudgetUpdateAmountHandler implements StateHandler {
    
    private final BudgetService budgetService;

    private final UserBotStateService userBotStateService;

    private final MainMenuKeyboardFactory mainMenuKeyboardFactory;

    private final TelegramMessageService telegramMessageService;

    @Override
    public boolean supports(UserBotState userBotState) {
        return userBotState.getState() == BotState.WAITING_BUDGET_UPDATE_AMOUNT;
    }

    @Override
    public void handle(Update update, UserBotState userBotState) {
        BigDecimal amount = parseAmount(update.getMessage().getText());
        if (amount == null) {
            sendInvalidAmountMessage(update.getMessage().getChatId());
            return;
        }
        BudgetResponseDto budget = budgetService.updateAmount(
                userBotState.getUserId(),
                userBotState.getSelectedBudgetId(),
                amount
        );
        userBotStateService.reset(userBotState.getUserId());
        sendBudgetUpdatedMessage(update.getMessage().getChatId(), budget);
    }

    
    private BigDecimal parseAmount(String value) {
        try {
            BigDecimal amount = new BigDecimal(value.trim().replace(",", "."));
            return amount.signum() > 0 ? amount : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private void sendBudgetUpdatedMessage(
            Long chatId,
            BudgetResponseDto budget
    ) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("""
                        ✅ Бюджет обновлён.
                        %s %s
                        Новый лимит: %s ₽
                        """.formatted(
                        budget.categoryEmoji(),
                        budget.categoryName(),
                        budget.amount()
                ))
                .replyMarkup(mainMenuKeyboardFactory.create())
                .build();

        telegramMessageService.sendMessage(message);
    }

    private void sendInvalidAmountMessage(Long chatId) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("Введите положительное число, например: 30000 или 30000.50")
                .build();

        telegramMessageService.sendMessage(message);
    }
}