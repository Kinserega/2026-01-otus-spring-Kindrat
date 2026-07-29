package ru.otus.hw.finance_service.telegram.handler.callback;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.otus.hw.finance_service.dto.auth.UserResponseDto;
import ru.otus.hw.finance_service.dto.statistics.CategoryStatisticsDto;
import ru.otus.hw.finance_service.dto.statistics.FinanceStatisticsDto;
import ru.otus.hw.finance_service.enums.StatisticsPeriod;
import ru.otus.hw.finance_service.service.FinanceStatisticsService;
import ru.otus.hw.finance_service.service.TelegramUserService;
import ru.otus.hw.finance_service.telegram.keyboard.Constants;
import ru.otus.hw.finance_service.dto.statistics.BudgetStatisticsDto;
import ru.otus.hw.finance_service.telegram.service.TelegramMessageService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StatisticsCallbackHandler implements CallbackHandler {

    private final TelegramUserService telegramUserService;
    
    private final FinanceStatisticsService financeStatisticsService;

    private final TelegramMessageService telegramMessageService;
    
    @Override
    public boolean supports(String callbackData) {
        return Constants.TODAY_STATISTICS_CALLBACK.equals(callbackData)
                || Constants.CURRENT_MONTH_STATISTICS_CALLBACK.equals(callbackData);
    }

    @Override
    public void handle(Update update) {
        UserResponseDto user = telegramUserService.getOrCreateUser(update.getCallbackQuery().getFrom());

        StatisticsPeriod statisticsPeriod = resolveStatisticsPeriod(update.getCallbackQuery().getData());

        FinanceStatisticsDto statistics = financeStatisticsService.getStatistics(user.id(), statisticsPeriod);

        SendMessage message = SendMessage.builder()
                .chatId(update.getCallbackQuery().getMessage().getChatId())
                .text(buildStatisticsMessage(statistics, statisticsPeriod))
                .build();

        telegramMessageService.sendMessage(message);
    }

    private StatisticsPeriod resolveStatisticsPeriod(String callbackData) {
        return Constants.TODAY_STATISTICS_CALLBACK.equals(callbackData)
                ? StatisticsPeriod.TODAY
                : StatisticsPeriod.CURRENT_MONTH;
    }
    
    private String buildStatisticsMessage(FinanceStatisticsDto statistics, StatisticsPeriod statisticsPeriod) {
        return """
            📊 %s
            ➕ Доходы: %s ₽
            ➖ Расходы: %s ₽
            💰 Баланс: %s ₽

            %s
            %s
            """.formatted(
                resolvePeriodName(statisticsPeriod),
                statistics.totalIncome(),
                statistics.totalExpense(),
                statistics.balance(),
                buildCategoryStatistics(statistics.expenseCategories()),
                buildBudgetStatistics(statistics.budgets())
        );
    }

    private String buildCategoryStatistics(List<CategoryStatisticsDto> categories) {
        if (categories.isEmpty()) {
            return "Расходов по категориям пока нет.";
        }
        StringBuilder messageBuilder = new StringBuilder("Расходы по категориям:\n");
        categories.forEach(category ->
                messageBuilder.append(
                        "%s %s — %s ₽\n".formatted(
                                category.categoryEmoji(),
                                category.categoryName(),
                                category.amount()
                        )
                )
        );
        return messageBuilder.toString();
    }

    
    private String resolvePeriodName(StatisticsPeriod statisticsPeriod) {
        return statisticsPeriod == StatisticsPeriod.TODAY
                ? "Статистика за сегодня"
                : "Статистика за текущий месяц";
    }

    
    private String buildBudgetStatistics(List<BudgetStatisticsDto> budgets) {
        if (budgets.isEmpty()) {
            return "";
        }
        StringBuilder messageBuilder = new StringBuilder("💰 Использование бюджетов:\n\n");

        budgets.forEach(budget -> messageBuilder.append(buildBudgetMessage(budget)));

        return messageBuilder.toString();
    }

    
    private String buildBudgetMessage(BudgetStatisticsDto budget) {
        if (budget.exceeded()) {
            return """
                %s %s
                Лимит: %s ₽
                Потрачено: %s ₽
                Использовано: %d%%
                ⚠️ Превышение: %s ₽

                """.formatted(
                    budget.categoryEmoji(),
                    budget.categoryName(),
                    budget.limit(),
                    budget.spent(),
                    budget.usagePercentage(),
                    budget.remaining().abs()
            );
        }
        return """
            %s %s
            Лимит: %s ₽
            Потрачено: %s ₽
            Использовано: %d%%
            Осталось: %s ₽

            """.formatted(
                budget.categoryEmoji(),
                budget.categoryName(),
                budget.limit(),
                budget.spent(),
                budget.usagePercentage(),
                budget.remaining()
        );
    }
}