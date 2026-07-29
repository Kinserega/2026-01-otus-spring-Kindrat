package ru.otus.hw.finance_service.dto.report;

public record CategoryExpenseReportRowDto(
        String category,
        String amount,
        String percentage
) {
}