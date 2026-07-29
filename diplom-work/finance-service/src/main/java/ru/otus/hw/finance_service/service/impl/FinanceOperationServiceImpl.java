package ru.otus.hw.finance_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.finance_service.dto.operation.FinanceOperationResponseDto;
import ru.otus.hw.finance_service.entity.Category;
import ru.otus.hw.finance_service.entity.FinanceOperation;
import ru.otus.hw.finance_service.enums.FinanceOperationType;
import ru.otus.hw.finance_service.exception.CategoryNotFoundException;
import ru.otus.hw.finance_service.exception.FinanceOperationNotFoundException;
import ru.otus.hw.finance_service.mapper.FinanceOperationMapper;
import ru.otus.hw.finance_service.repository.CategoryRepository;
import ru.otus.hw.finance_service.repository.FinanceOperationRepository;
import ru.otus.hw.finance_service.service.CategoryService;
import ru.otus.hw.finance_service.service.FinanceOperationService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FinanceOperationServiceImpl implements FinanceOperationService {

    private static final int OPERATIONS_PAGE_SIZE = 3;

    private final FinanceOperationRepository financeOperationRepository;
    private final FinanceOperationMapper financeOperationMapper;
    private final CategoryService categoryService;

    @Override
    @Transactional
    public void createOperation(
            Long userId,
            Long categoryId,
            BigDecimal amount,
            FinanceOperationType operationType
    ) {
        Category category = categoryService.findCategory(userId, categoryId);
        validateCategoryOperationType(category, operationType);

        FinanceOperation financeOperation = new FinanceOperation();
        financeOperation.setUserId(userId);
        financeOperation.setCategory(category);
        financeOperation.setOperationType(operationType);
        financeOperation.setAmount(amount);
        financeOperation.setOperationDate(LocalDateTime.now());

        financeOperationRepository.save(financeOperation);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FinanceOperationResponseDto> findRecentOperations(Long userId, int page) {
        Pageable pageable = PageRequest.of(
                page,
                OPERATIONS_PAGE_SIZE,
                Sort.by(Sort.Direction.DESC, "operationDate")
        );
        return financeOperationRepository.findByUserId(userId, pageable)
                .map(financeOperationMapper::toResponseDto);
    }

    @Override
    @Transactional
    public void deleteOperation(Long userId, Long operationId) {
        FinanceOperation financeOperation = findOperation(userId, operationId);

        financeOperationRepository.delete(financeOperation);
    }

    @Override
    @Transactional
    public void updateOperationAmount(
            Long userId,
            Long operationId,
            BigDecimal amount
    ) {
        FinanceOperation financeOperation = findOperation(userId, operationId);
        financeOperation.setAmount(amount);
    }

    private void validateCategoryOperationType(
            Category category,
            FinanceOperationType operationType
    ) {
        if (category.getOperationType() != operationType) {
            throw new IllegalArgumentException("Категория не соответствует типу финансовой операции");
        }
    }

    private FinanceOperation findOperation(Long userId, Long operationId) {
        return financeOperationRepository.findByIdAndUserId(operationId, userId)
                .orElseThrow(() -> new FinanceOperationNotFoundException("Финансовая операция с ID %d не найдена".formatted(operationId)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FinanceOperationResponseDto> findOperationsByPeriod(
            Long userId,
            LocalDateTime periodFrom,
            LocalDateTime periodTo
    ) {
        List<FinanceOperation> operations =
                financeOperationRepository
                        .findAllByUserIdAndOperationDateGreaterThanEqualAndOperationDateLessThanOrderByOperationDateDesc(
                                userId,
                                periodFrom,
                                periodTo
                        );

        return financeOperationMapper.toResponseDtoList(operations);
    }
}