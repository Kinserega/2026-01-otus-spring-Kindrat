package ru.otus.hw.finance_service.dto.budget;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record BudgetRequestDto(

        @NotNull(message = "Идентификатор категории обязателен")
        Long categoryId,

        @NotNull(message = "Сумма бюджета обязательна")
        @DecimalMin(value = "0.01", message = "Сумма бюджета должна быть больше нуля")
        BigDecimal amount
) {
}