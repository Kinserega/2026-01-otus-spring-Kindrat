package ru.otus.hw.finance_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.finance_service.dto.budget.BudgetDataDto;
import ru.otus.hw.finance_service.dto.budget.BudgetResponseDto;
import ru.otus.hw.finance_service.entity.Budget;
import ru.otus.hw.finance_service.entity.Category;
import ru.otus.hw.finance_service.enums.FinanceOperationType;
import ru.otus.hw.finance_service.exception.BudgetNotFoundException;
import ru.otus.hw.finance_service.exception.CategoryNotFoundException;
import ru.otus.hw.finance_service.mapper.BudgetMapper;
import ru.otus.hw.finance_service.repository.BudgetRepository;
import ru.otus.hw.finance_service.repository.CategoryRepository;
import ru.otus.hw.finance_service.repository.FinanceOperationRepository;
import ru.otus.hw.finance_service.service.BudgetService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;

    private final CategoryRepository categoryRepository;

    private final FinanceOperationRepository financeOperationRepository;

    private final BudgetMapper budgetMapper;

    @Override
    @Transactional
    public BudgetResponseDto setCurrentMonthBudget(
            Long userId,
            Long categoryId,
            BigDecimal amount
    ) {
        validateAmount(amount);

        Category category = findExpenseCategory(userId, categoryId);
        String currentPeriod = resolveCurrentPeriod();

        Budget budget = budgetRepository
                .findByUserIdAndCategoryIdAndPeriod(userId, categoryId, currentPeriod)
                .orElseGet(Budget::new);

        budget.setUserId(userId);
        budget.setCategory(category);
        budget.setPeriod(currentPeriod);
        budget.setAmount(amount);

        Budget savedBudget = budgetRepository.save(budget);
        return buildBudgetResponse(savedBudget);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BudgetResponseDto> findCurrentMonthBudgets(Long userId) {
        return budgetRepository.findAllByUserIdAndPeriodOrderByCategoryName(
                        userId,
                        resolveCurrentPeriod()
                )
                .stream()
                .map(this::buildBudgetResponse)
                .toList();
    }

    @Override
    @Transactional
    public BudgetResponseDto updateAmount(
            Long userId,
            Long budgetId,
            BigDecimal amount
    ) {
        validateAmount(amount);

        Budget budget = findBudget(userId, budgetId);
        budget.setAmount(amount);

        return buildBudgetResponse(budget);
    }

    @Override
    @Transactional
    public void delete(Long userId, Long budgetId) {
        Budget budget = findBudget(userId, budgetId);
        budgetRepository.delete(budget);
    }

    private BudgetResponseDto buildBudgetResponse(Budget budget) {
        BudgetPeriodRange periodRange = resolvePeriodRange(budget.getPeriod());

        BigDecimal spent = financeOperationRepository.calculateCategoryTotalAmount(
                budget.getUserId(),
                budget.getCategory().getId(),
                FinanceOperationType.EXPENSE,
                periodRange.periodFrom(),
                periodRange.periodTo()
        );

        BigDecimal remaining = budget.getAmount().subtract(spent);
        boolean exceeded = remaining.signum() < 0;

        BudgetDataDto budgetData = budgetMapper.toDataDto(budget);

        return budgetMapper.toResponseDto(
                budgetData,
                spent,
                remaining,
                exceeded
        );
    }

    private Category findExpenseCategory(Long userId, Long categoryId) {
        Category category = categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new CategoryNotFoundException("Категория с ID %d не найдена".formatted(categoryId)));
        if (category.getOperationType() != FinanceOperationType.EXPENSE) {
            throw new IllegalArgumentException("Бюджет можно установить только для категории расходов");
        }

        if (!category.isActive()) {
            throw new IllegalArgumentException("Нельзя установить бюджет для неактивной категории");
        }

        return category;
    }

    private Budget findBudget(Long userId, Long budgetId) {
        return budgetRepository.findByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new BudgetNotFoundException("Бюджет с ID %d не найден".formatted(budgetId)));
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Сумма бюджета должна быть больше нуля");
        }
    }

    private String resolveCurrentPeriod() {
        return YearMonth.now().toString();
    }

    private BudgetPeriodRange resolvePeriodRange(String period) {
        YearMonth yearMonth = YearMonth.parse(period);

        return new BudgetPeriodRange(
                yearMonth.atDay(1).atStartOfDay(),
                yearMonth.plusMonths(1).atDay(1).atStartOfDay()
        );
    }

    private record BudgetPeriodRange(
            LocalDateTime periodFrom,
            LocalDateTime periodTo
    ) {
    }
}