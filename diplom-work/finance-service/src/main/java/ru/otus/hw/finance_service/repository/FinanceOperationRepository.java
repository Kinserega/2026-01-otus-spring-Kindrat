package ru.otus.hw.finance_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.otus.hw.finance_service.entity.FinanceOperation;
import ru.otus.hw.finance_service.enums.FinanceOperationType;
import ru.otus.hw.finance_service.repository.projection.CategoryStatisticsProjection;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FinanceOperationRepository extends JpaRepository<FinanceOperation, Long> {

    Page<FinanceOperation> findByUserId(Long userId, Pageable pageable);

    Optional<FinanceOperation> findByIdAndUserId(Long id, Long userId);

    @Query("""
        select coalesce(sum(operation.amount), 0)
        from FinanceOperation operation
        where operation.userId = :userId
          and operation.operationType = :operationType
          and operation.operationDate >= :periodFrom
          and operation.operationDate < :periodTo
        """)
    BigDecimal calculateTotalAmount(
            @Param("userId") Long userId,
            @Param("operationType") FinanceOperationType operationType,
            @Param("periodFrom") LocalDateTime periodFrom,
            @Param("periodTo") LocalDateTime periodTo
    );

    @Query("""
        select
            operation.category.name as categoryName,
            operation.category.emoji as categoryEmoji,
            sum(operation.amount) as amount
        from FinanceOperation operation
        where operation.userId = :userId
          and operation.operationType = :operationType
          and operation.operationDate >= :periodFrom
          and operation.operationDate < :periodTo
        group by operation.category.id,
                 operation.category.name,
                 operation.category.emoji
        order by sum(operation.amount) desc
        """)
    List<CategoryStatisticsProjection> calculateCategoryStatistics(
            @Param("userId") Long userId,
            @Param("operationType") FinanceOperationType operationType,
            @Param("periodFrom") LocalDateTime periodFrom,
            @Param("periodTo") LocalDateTime periodTo
    );

    @Query("""
        select coalesce(sum(operation.amount), 0)
        from FinanceOperation operation
        where operation.userId = :userId
          and operation.category.id = :categoryId
          and operation.operationType = :operationType
          and operation.operationDate >= :periodFrom
          and operation.operationDate < :periodTo
        """)
    BigDecimal calculateCategoryTotalAmount(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId,
            @Param("operationType") FinanceOperationType operationType,
            @Param("periodFrom") LocalDateTime periodFrom,
            @Param("periodTo") LocalDateTime periodTo
    );

    List<FinanceOperation> findAllByUserIdAndOperationDateGreaterThanEqualAndOperationDateLessThanOrderByOperationDateDesc(
            Long userId,
            LocalDateTime periodFrom,
            LocalDateTime periodTo
    );
}