package ru.otus.hw.finance_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.otus.hw.finance_service.dto.budget.BudgetDataDto;
import ru.otus.hw.finance_service.dto.budget.BudgetResponseDto;
import ru.otus.hw.finance_service.entity.Budget;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface BudgetMapper {

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "categoryEmoji", source = "category.emoji")
    BudgetDataDto toDataDto(Budget budget);

    BudgetResponseDto toResponseDto(
            BudgetDataDto budgetData,
            BigDecimal spent,
            BigDecimal remaining,
            boolean exceeded
    );
}