package ru.otus.hw.finance_service.dto.category;

import ru.otus.hw.finance_service.enums.FinanceOperationType;

public record CategoryResponseDto(
        Long id,
        String name,
        String emoji,
        FinanceOperationType operationType,
        boolean systemCategory
) {
}