package ru.otus.hw.finance_service.service;

import ru.otus.hw.finance_service.dto.template.GeneratedDocumentDto;
import ru.otus.hw.finance_service.dto.template.TemplateRenderRequestDto;

public interface DocumentGenerationService {

    GeneratedDocumentDto generate(TemplateRenderRequestDto request);
}