package ru.otus.hw.finance_service.dto.operation;

import ru.otus.hw.finance_service.enums.FinanceOperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FinanceOperationResponseDto(
        Long id,
        FinanceOperationType operationType,
        String categoryName,
        String categoryEmoji,
        BigDecimal amount,
        LocalDateTime operationDate,
        String comment
) {
}