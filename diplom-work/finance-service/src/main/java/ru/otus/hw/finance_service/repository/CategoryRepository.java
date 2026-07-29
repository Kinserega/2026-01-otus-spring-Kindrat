package ru.otus.hw.finance_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.finance_service.entity.Category;
import ru.otus.hw.finance_service.enums.FinanceOperationType;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByUserIdAndNameAndOperationType(Long userId, String name, FinanceOperationType operationType);

    List<Category> findAllByUserIdAndOperationTypeAndActiveTrueOrderByName(Long userId, FinanceOperationType operationType);

    Optional<Category> findByIdAndUserId(Long id, Long userId);
}