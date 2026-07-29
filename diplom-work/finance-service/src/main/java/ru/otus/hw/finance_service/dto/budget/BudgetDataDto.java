package ru.otus.hw.finance_service.dto.budget;

import java.math.BigDecimal;

public record BudgetDataDto(
        Long id,
        Long categoryId,
        String categoryName,
        String categoryEmoji,
        String period,
        BigDecimal amount
) {
}