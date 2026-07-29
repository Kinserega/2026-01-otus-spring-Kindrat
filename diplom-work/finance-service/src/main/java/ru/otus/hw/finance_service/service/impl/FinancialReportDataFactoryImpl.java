package ru.otus.hw.finance_service.service.impl;

import org.springframework.stereotype.Component;
import ru.otus.hw.finance_service.dto.auth.UserResponseDto;
import ru.otus.hw.finance_service.dto.budget.BudgetResponseDto;
import ru.otus.hw.finance_service.dto.operation.FinanceOperationResponseDto;
import ru.otus.hw.finance_service.dto.report.BudgetReportRowDto;
import ru.otus.hw.finance_service.dto.report.CategoryExpenseReportRowDto;
import ru.otus.hw.finance_service.dto.report.ChartDataDto;
import ru.otus.hw.finance_service.dto.report.OperationReportRowDto;
import ru.otus.hw.finance_service.dto.statistics.CategoryStatisticsDto;
import ru.otus.hw.finance_service.dto.statistics.FinanceStatisticsDto;
import ru.otus.hw.finance_service.enums.FinanceOperationType;
import ru.otus.hw.finance_service.enums.StatisticsPeriod;
import ru.otus.hw.finance_service.service.FinancialReportDataFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FinancialReportDataFactoryImpl implements FinancialReportDataFactory {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private static final int MONEY_SCALE = 2;

    private static final String PIE_CHART_TYPE = "PIE";

    @Override
    public Map<String, Object> create(
            UserResponseDto user,
            FinanceStatisticsDto statistics,
            List<FinanceOperationResponseDto> operations,
            List<BudgetResponseDto> budgets,
            StatisticsPeriod statisticsPeriod
    ) {
        Map<String, Object> renderData = new HashMap<>();

        renderData.put("fullName", buildFullName(user));
        renderData.put("periodName", resolvePeriodName(statisticsPeriod));
        renderData.put("periodFrom", formatDateTime(statistics.periodFrom()));
        renderData.put("periodTo", formatDateTime(statistics.periodTo()));
        renderData.put("generatedAt", formatDateTime(LocalDateTime.now()));

        renderData.put("totalIncome", formatMoney(statistics.totalIncome()));
        renderData.put("totalExpense", formatMoney(statistics.totalExpense()));
        renderData.put("balance", formatMoney(statistics.balance()));

        renderData.put(
                "categoryExpenses",
                buildCategoryExpenseRows(statistics)
        );
        renderData.put(
                "operations",
                buildOperationRows(operations)
        );
        renderData.put(
                "budgets",
                buildBudgetRows(budgets)
        );
        renderData.put(
                "expenseChart",
                buildExpenseChart(statistics.expenseCategories())
        );

        return renderData;
    }

    private String buildFullName(UserResponseDto user) {
        String firstName = normalizeText(user.firstName());
        String lastName = normalizeText(user.lastName());

        String fullName = "%s %s".formatted(firstName, lastName).trim();

        return fullName.isBlank()
                ? user.username()
                : fullName;
    }

    private List<CategoryExpenseReportRowDto> buildCategoryExpenseRows(
            FinanceStatisticsDto statistics
    ) {
        return statistics.expenseCategories()
                .stream()
                .map(category -> new CategoryExpenseReportRowDto(
                        buildCategoryName(
                                category.categoryEmoji(),
                                category.categoryName()
                        ),
                        formatMoney(category.amount()),
                        formatPercentage(
                                calculateExpensePercentage(
                                        category.amount(),
                                        statistics.totalExpense()
                                )
                        )
                ))
                .toList();
    }

    private List<OperationReportRowDto> buildOperationRows(
            List<FinanceOperationResponseDto> operations
    ) {
        return operations.stream()
                .map(operation -> new OperationReportRowDto(
                        formatDateTime(operation.operationDate()),
                        resolveOperationTypeName(operation.operationType()),
                        buildCategoryName(
                                operation.categoryEmoji(),
                                operation.categoryName()
                        ),
                        formatMoney(operation.amount()),
                        normalizeText(operation.comment())
                ))
                .toList();
    }

    private List<BudgetReportRowDto> buildBudgetRows(
            List<BudgetResponseDto> budgets
    ) {
        return budgets.stream()
                .map(budget -> new BudgetReportRowDto(
                        buildCategoryName(
                                budget.categoryEmoji(),
                                budget.categoryName()
                        ),
                        formatMoney(budget.amount()),
                        formatMoney(budget.spent()),
                        formatMoney(budget.remaining().abs()),
                        calculateBudgetUsagePercentage(budget) + "%",
                        budget.exceeded()
                                ? "Бюджет превышен"
                                : "В пределах лимита"
                ))
                .toList();
    }

    private ChartDataDto buildExpenseChart(
            List<CategoryStatisticsDto> categories
    ) {
        return new ChartDataDto(
                PIE_CHART_TYPE,
                "Расходы по категориям",
                categories.stream()
                        .map(CategoryStatisticsDto::categoryName)
                        .toList(),
                categories.stream()
                        .map(CategoryStatisticsDto::amount)
                        .toList()
        );
    }

    private BigDecimal calculateExpensePercentage(
            BigDecimal categoryAmount,
            BigDecimal totalExpense
    ) {
        if (totalExpense == null || totalExpense.signum() == 0) {
            return BigDecimal.ZERO;
        }

        return categoryAmount
                .multiply(BigDecimal.valueOf(100))
                .divide(totalExpense, 2, RoundingMode.HALF_UP);
    }

    private int calculateBudgetUsagePercentage(BudgetResponseDto budget) {
        if (budget.amount() == null || budget.amount().signum() <= 0) {
            return 0;
        }

        return budget.spent()
                .multiply(BigDecimal.valueOf(100))
                .divide(budget.amount(), 0, RoundingMode.HALF_UP)
                .intValue();
    }

    private String resolveOperationTypeName(
            FinanceOperationType operationType
    ) {
        return operationType == FinanceOperationType.INCOME
                ? "Доход"
                : "Расход";
    }

    private String resolvePeriodName(StatisticsPeriod statisticsPeriod) {
        return statisticsPeriod == StatisticsPeriod.TODAY
                ? "За текущий день"
                : "За текущий месяц";
    }

    private String buildCategoryName(
            String emoji,
            String categoryName
    ) {
        return "%s %s".formatted(
                normalizeText(emoji),
                normalizeText(categoryName)
        ).trim();
    }

    private String formatMoney(BigDecimal amount) {
        if (amount == null) {
            return BigDecimal.ZERO
                    .setScale(MONEY_SCALE, RoundingMode.HALF_UP)
                    .toPlainString();
        }

        return amount
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP)
                .toPlainString();
    }

    private String formatPercentage(BigDecimal percentage) {
        return percentage
                .setScale(2, RoundingMode.HALF_UP)
                .toPlainString() + "%";
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime == null
                ? ""
                : dateTime.format(DATE_TIME_FORMATTER);
    }

    private String normalizeText(String value) {
        return value == null ? "" : value.trim();
    }
}