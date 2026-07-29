package ru.otus.hw.finance_service.dto.report;

public record OperationReportRowDto(
        String operationDate,
        String operationType,
        String category,
        String amount,
        String comment
) {
}