package ru.otus.hw.finance_service.service;

import org.springframework.data.domain.Page;
import ru.otus.hw.finance_service.dto.operation.FinanceOperationResponseDto;
import ru.otus.hw.finance_service.enums.FinanceOperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface FinanceOperationService {

    void createOperation(
            Long userId,
            Long categoryId,
            BigDecimal amount,
            FinanceOperationType operationType
    );

    Page<FinanceOperationResponseDto> findRecentOperations(
            Long userId,
            int page
    );

    void updateOperationAmount(
            Long userId,
            Long operationId,
            BigDecimal amount
    );

    void deleteOperation(Long userId, Long operationId);

    List<FinanceOperationResponseDto> findOperationsByPeriod(
            Long userId,
            LocalDateTime periodFrom,
            LocalDateTime periodTo
    );
}