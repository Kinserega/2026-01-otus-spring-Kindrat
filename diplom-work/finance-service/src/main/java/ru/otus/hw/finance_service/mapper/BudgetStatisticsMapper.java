package ru.otus.hw.finance_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.otus.hw.finance_service.dto.budget.BudgetResponseDto;
import ru.otus.hw.finance_service.dto.statistics.BudgetStatisticsDto;

@Mapper(componentModel = "spring")
public interface BudgetStatisticsMapper {

    @Mapping(target = "limit", source = "budget.amount")
    @Mapping(target = "usagePercentage", source = "usagePercentage")
    BudgetStatisticsDto toStatisticsDto(
            BudgetResponseDto budget,
            int usagePercentage
    );
}