package ru.otus.hw.finance_service.dto.template;

import ru.otus.hw.finance_service.enums.DocumentFormat;

import java.util.Map;

public record TemplateRenderRequestDto(
        String templateCode,
        DocumentFormat outputFormat,
        Map<String, Object> data
) {
}