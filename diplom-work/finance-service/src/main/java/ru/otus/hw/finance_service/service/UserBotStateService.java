package ru.otus.hw.finance_service.service;

import ru.otus.hw.finance_service.entity.UserBotState;
import ru.otus.hw.finance_service.enums.FinanceOperationType;

import java.util.Optional;

public interface UserBotStateService {

    void waitOperationAmount(Long userId, Long categoryId,
                           FinanceOperationType operationType);

    Optional<UserBotState> findByUserId(Long userId);

    void waitOperationUpdateAmount(Long userId, Long operationId);

    void reset(Long userId);

    void waitCategoryName(Long userId, FinanceOperationType operationType);

    void waitCategoryEmoji(Long userId, String categoryName);

    void waitCategoryRename(Long userId, Long categoryId);

    void waitBudgetAmount(Long userId, Long categoryId);

    void waitBudgetUpdateAmount(Long userId, Long budgetId);
}