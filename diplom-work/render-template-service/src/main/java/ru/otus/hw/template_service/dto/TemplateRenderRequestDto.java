package ru.otus.hw.template_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import ru.otus.hw.template_service.enums.DocumentFormat;

import java.util.Map;

public record TemplateRenderRequestDto(

        @NotBlank(message = "Код шаблона обязателен")
        String templateCode,

        @NotNull(message = "Формат документа обязателен")
        DocumentFormat outputFormat,

        @NotEmpty(message = "Данные шаблона не должны быть пустыми")
        Map<String, Object> data
) {
}