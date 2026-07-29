package ru.otus.hw.finance_service.dto.budget;

import java.math.BigDecimal;

public record BudgetResponseDto(
        Long id,
        Long categoryId,
        String categoryName,
        String categoryEmoji,
        String period,
        BigDecimal amount,
        BigDecimal spent,
        BigDecimal remaining,
        boolean exceeded
) {
}