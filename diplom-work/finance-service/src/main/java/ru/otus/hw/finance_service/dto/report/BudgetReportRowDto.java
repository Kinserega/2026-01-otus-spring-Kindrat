package ru.otus.hw.finance_service.dto.report;

public record BudgetReportRowDto(
        String category,
        String limit,
        String spent,
        String remaining,
        String usagePercentage,
        String status
) {
}