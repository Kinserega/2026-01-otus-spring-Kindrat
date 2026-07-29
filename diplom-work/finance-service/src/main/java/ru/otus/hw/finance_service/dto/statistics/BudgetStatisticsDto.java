package ru.otus.hw.finance_service.dto.statistics;

import java.math.BigDecimal;

public record BudgetStatisticsDto(
        String categoryName,
        String categoryEmoji,
        BigDecimal limit,
        BigDecimal spent,
        BigDecimal remaining,
        int usagePercentage,
        boolean exceeded
) {
}