package ru.otus.hw.finance_service.dto.statistics;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record FinanceStatisticsDto(
        LocalDateTime periodFrom,
        LocalDateTime periodTo,
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal balance,
        List<CategoryStatisticsDto> expenseCategories,
        List<BudgetStatisticsDto> budgets
) {
}