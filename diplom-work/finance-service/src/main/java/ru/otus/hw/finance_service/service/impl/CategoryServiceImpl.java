package ru.otus.hw.finance_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.finance_service.dto.category.CategoryResponseDto;
import ru.otus.hw.finance_service.entity.Category;
import ru.otus.hw.finance_service.enums.DefaultCategory;
import ru.otus.hw.finance_service.enums.FinanceOperationType;
import ru.otus.hw.finance_service.exception.CategoryNotFoundException;
import ru.otus.hw.finance_service.mapper.CategoryMapper;
import ru.otus.hw.finance_service.repository.CategoryRepository;
import ru.otus.hw.finance_service.service.CategoryService;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public void initializeDefaultCategories(Long userId) {
        Arrays.stream(DefaultCategory.values())
                .filter(category -> !categoryExists(userId, category))
                .map(category -> createCategory(userId, category))
                .forEach(categoryRepository::save);
    }

    @Override
    @Transactional
    public Category findCategory(Long userId, Long categoryId) {
        return categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new CategoryNotFoundException("Категория с ID %d не найдена".formatted(categoryId)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDto> findActiveCategories(
            Long userId,
            FinanceOperationType operationType
    ) {
        return categoryMapper.toResponseDtoList(
                categoryRepository.findAllByUserIdAndOperationTypeAndActiveTrueOrderByName(userId, operationType)
        );
    }

    private boolean categoryExists(Long userId, DefaultCategory defaultCategory) {
        return categoryRepository.existsByUserIdAndNameAndOperationType(
                userId,
                defaultCategory.getDisplayName(),
                defaultCategory.getOperationType()
        );
    }

    private Category createCategory(Long userId, DefaultCategory defaultCategory) {
        Category category = new Category();
        category.setUserId(userId);
        category.setName(defaultCategory.getDisplayName());
        category.setEmoji(defaultCategory.getEmoji());
        category.setOperationType(defaultCategory.getOperationType());
        category.setSystemCategory(true);
        category.setActive(true);
        return category;
    }

    /**
     * Создаёт пользовательскую категорию.
     *
     * @param userId идентификатор пользователя
     * @param name название категории
     * @param emoji emoji категории
     * @param operationType тип финансовой операции
     * @return созданная категория
     */
    @Override
    @Transactional
    public CategoryResponseDto createCategory(Long userId, String name, String emoji, FinanceOperationType operationType) {
        validateCategoryDoesNotExist(userId, name, operationType);

        Category category = new Category();
        category.setUserId(userId);
        category.setName(name);
        category.setEmoji(emoji);
        category.setOperationType(operationType);
        category.setSystemCategory(false);
        category.setActive(true);

        return categoryMapper.toResponseDto(categoryRepository.save(category));
    }

    /**
     * Проверяет отсутствие категории с указанным названием.
     *
     * @param userId идентификатор пользователя
     * @param name название категории
     * @param operationType тип финансовой операции
     */
    private void validateCategoryDoesNotExist(Long userId, String name, FinanceOperationType operationType) {
        if (categoryRepository.existsByUserIdAndNameAndOperationType(userId, name, operationType)) {
            throw new IllegalArgumentException("Категория с названием %s уже существует".formatted(name));
        }
    }

    /**
     * Возвращает категорию пользователя по идентификатору.
     *
     * @param userId идентификатор пользователя
     * @param categoryId идентификатор категории
     * @return категория пользователя
     */
    @Override
    @Transactional(readOnly = true)
    public CategoryResponseDto findById(Long userId, Long categoryId) {
        return categoryMapper.toResponseDto(findCategory(userId, categoryId));
    }

    /**
     * Удаляет пользовательскую категорию.
     *
     * @param userId идентификатор пользователя
     * @param categoryId идентификатор категории
     */
    @Override
    @Transactional
    public void deleteCategory(Long userId, Long categoryId) {
        Category category = findCategory(userId, categoryId);
        validateCategoryCanBeDeleted(category);
        category.setActive(false);
    }

    /**
     * Изменяет название пользовательской категории.
     *
     * @param userId идентификатор пользователя
     * @param categoryId идентификатор категории
     * @param name новое название категории
     * @return обновлённая категория
     */
    @Override
    @Transactional
    public CategoryResponseDto renameCategory(Long userId, Long categoryId, String name) {
        Category category = findCategory(userId, categoryId);

        validateCategoryCanBeModified(category);
        validateCategoryDoesNotExist(userId, name, category.getOperationType());
        category.setName(name);

        return categoryMapper.toResponseDto(category);
    }

    /**
     * Проверяет возможность изменения категории.
     *
     * @param category категория пользователя
     */
    private void validateCategoryCanBeModified(Category category) {
        if (category.isSystemCategory()) {
            throw new IllegalArgumentException(
                    "Системную категорию изменять нельзя"
            );
        }
    }

    /**
     * Проверяет возможность удаления категории.
     *
     * @param category категория пользователя
     */
    private void validateCategoryCanBeDeleted(Category category) {
        if (category.isSystemCategory()) {
            throw new IllegalArgumentException("Системную категорию удалить нельзя");
        }
    }


}