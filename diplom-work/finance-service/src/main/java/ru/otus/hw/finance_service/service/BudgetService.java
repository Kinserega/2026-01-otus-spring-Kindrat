package ru.otus.hw.finance_service.service;

import ru.otus.hw.finance_service.dto.budget.BudgetResponseDto;

import java.math.BigDecimal;
import java.util.List;

public interface BudgetService {

    BudgetResponseDto setCurrentMonthBudget(
            Long userId,
            Long categoryId,
            BigDecimal amount
    );

    List<BudgetResponseDto> findCurrentMonthBudgets(Long userId);

    BudgetResponseDto updateAmount(
            Long userId,
            Long budgetId,
            BigDecimal amount
    );

    void delete(Long userId, Long budgetId);
}