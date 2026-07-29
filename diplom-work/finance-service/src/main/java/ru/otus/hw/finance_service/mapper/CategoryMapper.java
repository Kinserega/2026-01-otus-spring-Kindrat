package ru.otus.hw.finance_service.mapper;

import org.mapstruct.Mapper;
import ru.otus.hw.finance_service.dto.category.CategoryResponseDto;
import ru.otus.hw.finance_service.entity.Category;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryResponseDto toResponseDto(Category category);

    List<CategoryResponseDto> toResponseDtoList(List<Category> categories);
}