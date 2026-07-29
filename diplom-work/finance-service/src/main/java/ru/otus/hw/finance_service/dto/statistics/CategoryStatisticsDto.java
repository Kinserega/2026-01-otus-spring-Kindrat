package ru.otus.hw.finance_service.dto.statistics;

import java.math.BigDecimal;

public record CategoryStatisticsDto(
        String categoryName,
        String categoryEmoji,
        BigDecimal amount
) {
}