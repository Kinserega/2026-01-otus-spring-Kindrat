package ru.otus.hw.finance_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.finance_service.entity.Budget;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    Optional<Budget> findByUserIdAndCategoryIdAndPeriod(
            Long userId,
            Long categoryId,
            String period
    );

    Optional<Budget> findByIdAndUserId(
            Long id,
            Long userId
    );

    List<Budget> findAllByUserIdAndPeriodOrderByCategoryName(
            Long userId,
            String period
    );
}