package ru.otus.hw.finance_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.finance_service.dto.budget.BudgetResponseDto;
import ru.otus.hw.finance_service.dto.statistics.BudgetStatisticsDto;
import ru.otus.hw.finance_service.dto.statistics.CategoryStatisticsDto;
import ru.otus.hw.finance_service.dto.statistics.FinanceStatisticsDto;
import ru.otus.hw.finance_service.enums.FinanceOperationType;
import ru.otus.hw.finance_service.enums.StatisticsPeriod;
import ru.otus.hw.finance_service.mapper.BudgetStatisticsMapper;
import ru.otus.hw.finance_service.repository.FinanceOperationRepository;
import ru.otus.hw.finance_service.repository.projection.CategoryStatisticsProjection;
import ru.otus.hw.finance_service.service.BudgetService;
import ru.otus.hw.finance_service.service.FinanceStatisticsService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FinanceStatisticsServiceImpl implements FinanceStatisticsService {

    private final FinanceOperationRepository financeOperationRepository;

    private final BudgetService budgetService;


    private final BudgetStatisticsMapper budgetStatisticsMapper;

    @Override
    @Transactional(readOnly = true)
    public FinanceStatisticsDto getStatistics(Long userId, StatisticsPeriod statisticsPeriod) {
        StatisticsPeriodRange periodRange = resolvePeriodRange(statisticsPeriod);
        BigDecimal totalIncome = calculateTotalAmount(userId, FinanceOperationType.INCOME, periodRange);
        BigDecimal totalExpense = calculateTotalAmount(userId, FinanceOperationType.EXPENSE, periodRange);
        List<CategoryStatisticsDto> expenseCategories = getExpenseCategoryStatistics(userId, periodRange);
        List<BudgetStatisticsDto> budgets = getBudgetStatistics(userId, statisticsPeriod);
        return new FinanceStatisticsDto(
                periodRange.periodFrom(),
                periodRange.periodTo(),
                totalIncome,
                totalExpense,
                totalIncome.subtract(totalExpense),
                expenseCategories,
                budgets
        );
    }

    private BigDecimal calculateTotalAmount(Long userId, FinanceOperationType operationType, StatisticsPeriodRange periodRange) {
        return financeOperationRepository.calculateTotalAmount(
                userId,
                operationType,
                periodRange.periodFrom(),
                periodRange.periodTo()
        );
    }

    private List<CategoryStatisticsDto> getExpenseCategoryStatistics(Long userId, StatisticsPeriodRange periodRange) {
        return financeOperationRepository.calculateCategoryStatistics(
                        userId,
                        FinanceOperationType.EXPENSE,
                        periodRange.periodFrom(),
                        periodRange.periodTo()
                )
                .stream()
                .map(this::mapCategoryStatistics)
                .toList();
    }

    private CategoryStatisticsDto mapCategoryStatistics(CategoryStatisticsProjection projection) {
        return new CategoryStatisticsDto(
                projection.getCategoryName(),
                projection.getCategoryEmoji(),
                projection.getAmount()
        );
    }

    private StatisticsPeriodRange resolvePeriodRange(StatisticsPeriod statisticsPeriod) {
        return switch (statisticsPeriod) {
            case TODAY -> createTodayRange();
            case CURRENT_MONTH -> createCurrentMonthRange();
        };
    }

    private StatisticsPeriodRange createTodayRange() {
        LocalDate today = LocalDate.now();
        return new StatisticsPeriodRange(
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay()
        );
    }

    private StatisticsPeriodRange createCurrentMonthRange() {
        YearMonth currentMonth = YearMonth.now();
        return new StatisticsPeriodRange(
                currentMonth.atDay(1).atStartOfDay(),
                currentMonth.plusMonths(1).atDay(1).atStartOfDay()
        );
    }

    private record StatisticsPeriodRange(
            LocalDateTime periodFrom,
            LocalDateTime periodTo
    ) {
    }

    private List<BudgetStatisticsDto> getBudgetStatistics(Long userId, StatisticsPeriod statisticsPeriod) {
        if (statisticsPeriod != StatisticsPeriod.CURRENT_MONTH) {
            return Collections.emptyList();
        }

        return budgetService.findCurrentMonthBudgets(userId)
                .stream()
                .map(this::mapBudgetStatistics)
                .toList();
    }

    private BudgetStatisticsDto mapBudgetStatistics(BudgetResponseDto budget) {
        int usagePercentage = calculateUsagePercentage(budget.spent(), budget.amount());
        return budgetStatisticsMapper.toStatisticsDto(budget, usagePercentage);
    }

    private int calculateUsagePercentage(BigDecimal spent, BigDecimal limit) {
        if (limit == null || limit.signum() <= 0) {
            return 0;
        }
        return spent
                .multiply(BigDecimal.valueOf(100))
                .divide(limit, 0, RoundingMode.HALF_UP)
                .intValue();
    }
}