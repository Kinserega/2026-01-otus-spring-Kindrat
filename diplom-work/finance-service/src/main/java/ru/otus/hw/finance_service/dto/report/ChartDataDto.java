package ru.otus.hw.finance_service.dto.report;

import java.math.BigDecimal;
import java.util.List;

public record ChartDataDto(
        String type,
        String title,
        List<String> categories,
        List<BigDecimal> values
) {
}