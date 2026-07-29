package ru.otus.hw.finance_service.telegram.handler.callback;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.otus.hw.finance_service.dto.auth.UserResponseDto;
import ru.otus.hw.finance_service.dto.budget.BudgetResponseDto;
import ru.otus.hw.finance_service.service.BudgetService;
import ru.otus.hw.finance_service.service.TelegramUserService;
import ru.otus.hw.finance_service.telegram.keyboard.BudgetKeyboardFactory;
import ru.otus.hw.finance_service.telegram.keyboard.Constants;
import ru.otus.hw.finance_service.telegram.service.TelegramMessageService;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CurrentBudgetsCallbackHandler implements CallbackHandler {

    private final TelegramUserService telegramUserService;

    private final BudgetService budgetService;

    private final BudgetKeyboardFactory budgetKeyboardFactory;

    private final TelegramMessageService telegramMessageService;

    @Override
    public boolean supports(String callbackData) {
        return Constants.CURRENT_BUDGETS_CALLBACK.equals(callbackData);
    }

    @Override
    public void handle(Update update) {
        UserResponseDto user = telegramUserService.getOrCreateUser(
                update.getCallbackQuery().getFrom()
        );

        List<BudgetResponseDto> budgets = budgetService.findCurrentMonthBudgets(
                user.id()
        );

        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        if (budgets.isEmpty()) {
            sendEmptyBudgetsMessage(chatId);
            return;
        }

        sendBudgetsHeader(chatId);

        budgets.forEach(budget -> sendBudgetMessage(chatId, budget));
    }

    private void sendEmptyBudgetsMessage(Long chatId) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("На текущий месяц бюджеты пока не установлены.")
                .replyMarkup(budgetKeyboardFactory.createManagementMenu())
                .build();

        telegramMessageService.sendMessage(message);
    }

    private void sendBudgetsHeader(Long chatId) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("💰 Бюджеты текущего месяца:")
                .build();

        telegramMessageService.sendMessage(message);
    }

    private void sendBudgetMessage(
            Long chatId,
            BudgetResponseDto budget
    ) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(buildBudgetMessage(budget))
                .replyMarkup(
                        budgetKeyboardFactory.createBudgetActions(budget.id())
                )
                .build();

        telegramMessageService.sendMessage(message);
    }

    private String buildBudgetMessage(BudgetResponseDto budget) {
        return budget.exceeded()
                ? buildExceededBudgetMessage(budget)
                : buildActiveBudgetMessage(budget);
    }

    private String buildActiveBudgetMessage(BudgetResponseDto budget) {
        return """
                %s %s

                Лимит: %s ₽
                Потрачено: %s ₽
                Осталось: %s ₽
                """.formatted(
                budget.categoryEmoji(),
                budget.categoryName(),
                budget.amount(),
                budget.spent(),
                budget.remaining()
        );
    }

    private String buildExceededBudgetMessage(BudgetResponseDto budget) {
        BigDecimal exceededAmount = budget.remaining().abs();

        return """
                %s %s

                Лимит: %s ₽
                Потрачено: %s ₽

                ⚠️ Бюджет превышен на %s ₽
                """.formatted(
                budget.categoryEmoji(),
                budget.categoryName(),
                budget.amount(),
                budget.spent(),
                exceededAmount
        );
    }
}