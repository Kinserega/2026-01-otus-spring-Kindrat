package ru.otus.hw.finance_service.service;

import ru.otus.hw.finance_service.dto.category.CategoryResponseDto;
import ru.otus.hw.finance_service.entity.Category;
import ru.otus.hw.finance_service.enums.FinanceOperationType;

import java.util.List;

public interface CategoryService {

    void initializeDefaultCategories(Long userId);

    Category findCategory(Long userId, Long categoryId);

    List<CategoryResponseDto> findActiveCategories(
            Long userId,
            FinanceOperationType operationType
    );

    CategoryResponseDto createCategory(
            Long userId,
            String name,
            String emoji,
            FinanceOperationType operationType
    );

    CategoryResponseDto findById(Long userId, Long categoryId);

    void deleteCategory(Long userId, Long categoryId);

    CategoryResponseDto renameCategory(
            Long userId,
            Long categoryId,
            String name
    );
}