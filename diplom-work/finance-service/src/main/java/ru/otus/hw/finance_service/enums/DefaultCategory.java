package ru.otus.hw.finance_service.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DefaultCategory {

    FOOD("Еда", "🍔", FinanceOperationType.EXPENSE),
    TRANSPORT("Транспорт", "🚕", FinanceOperationType.EXPENSE),
    HOME("Дом", "🏠", FinanceOperationType.EXPENSE),
    HEALTH("Здоровье", "💊", FinanceOperationType.EXPENSE),
    SHOPPING("Покупки", "🛍", FinanceOperationType.EXPENSE),
    SALARY("Зарплата", "💰", FinanceOperationType.INCOME),
    GIFT("Подарок", "🎁", FinanceOperationType.INCOME),
    FREELANCE("Подработка", "💼", FinanceOperationType.INCOME),
    INTEREST("Проценты", "🏦", FinanceOperationType.INCOME);

    private final String displayName;

    private final String emoji;

    private final FinanceOperationType operationType;
}