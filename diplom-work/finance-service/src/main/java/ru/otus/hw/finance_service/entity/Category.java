package ru.otus.hw.finance_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.otus.hw.finance_service.entity.base.DateLifecycleEntityBase;
import ru.otus.hw.finance_service.enums.FinanceOperationType;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "categories",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_category_user_name_type", columnNames = {"user_id", "name", "operation_type"})
        }
)
public class Category extends DateLifecycleEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "emoji", length = 20)
    private String emoji;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", nullable = false, length = 20)
    private FinanceOperationType operationType;

    @Column(name = "system_category", nullable = false)
    private boolean systemCategory;

    @Column(name = "active", nullable = false)
    private boolean active = false;
}