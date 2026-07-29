package ru.otus.hw.finance_service.service;

import ru.otus.hw.finance_service.dto.auth.UserResponseDto;
import ru.otus.hw.finance_service.dto.budget.BudgetResponseDto;
import ru.otus.hw.finance_service.dto.operation.FinanceOperationResponseDto;
import ru.otus.hw.finance_service.dto.statistics.FinanceStatisticsDto;
import ru.otus.hw.finance_service.enums.StatisticsPeriod;

import java.util.List;
import java.util.Map;

public interface FinancialReportDataFactory {

    Map<String, Object> create(
            UserResponseDto user,
            FinanceStatisticsDto statistics,
            List<FinanceOperationResponseDto> operations,
            List<BudgetResponseDto> budgets,
            StatisticsPeriod statisticsPeriod
    );
}