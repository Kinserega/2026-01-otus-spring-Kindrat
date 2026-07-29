package ru.otus.hw.finance_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ru.otus.hw.finance_service.entity.base.DateLifecycleEntityBase;
import ru.otus.hw.finance_service.enums.BotState;
import ru.otus.hw.finance_service.enums.FinanceOperationType;


@Getter
@Setter
@Entity
@Table(name = "user_bot_states")
public class UserBotState extends DateLifecycleEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 50)
    private BotState state;

    @Column(name = "selected_operation_id")
    private Long selectedOperationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", length = 20)
    private FinanceOperationType operationType;

    @Column(name = "selected_category_id")
    private Long selectedCategoryId;

    @Column(name = "selected_budget_id")
    private Long selectedBudgetId;

    @Column(name = "temporary_category_name", length = 100)
    private String temporaryCategoryName;
}