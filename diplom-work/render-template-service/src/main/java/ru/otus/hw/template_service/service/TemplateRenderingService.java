package ru.otus.hw.template_service.service;

import ru.otus.hw.template_service.dto.TemplateRenderRequestDto;

public interface TemplateRenderingService {

    byte[] render(TemplateRenderRequestDto request);
}