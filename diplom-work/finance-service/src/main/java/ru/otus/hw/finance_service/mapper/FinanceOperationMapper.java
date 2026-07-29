package ru.otus.hw.finance_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.otus.hw.finance_service.dto.operation.FinanceOperationResponseDto;
import ru.otus.hw.finance_service.entity.FinanceOperation;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FinanceOperationMapper {

    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "categoryEmoji", source = "category.emoji")
    FinanceOperationResponseDto toResponseDto(FinanceOperation financeOperation);

    List<FinanceOperationResponseDto> toResponseDtoList(List<FinanceOperation> financeOperations);
}