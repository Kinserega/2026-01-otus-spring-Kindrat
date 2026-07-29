package ru.otus.hw.template_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.template_service.config.TemplateProperties;
import ru.otus.hw.template_service.dto.TemplateRenderRequestDto;
import ru.otus.hw.template_service.enums.DocumentFormat;
import ru.otus.hw.template_service.exception.TemplateRenderingException;
import ru.otus.hw.template_service.renderer.PoiTemplateRenderer;
import ru.otus.hw.template_service.renderer.TemplateLoader;
import ru.otus.hw.template_service.renderer.TemplateRenderDataProcessor;
import ru.otus.hw.template_service.service.TemplateRegistry;
import ru.otus.hw.template_service.service.TemplateRenderingService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TemplateRenderingServiceImpl implements TemplateRenderingService {

    private final TemplateRegistry templateRegistry;

    private final TemplateLoader templateLoader;

    private final TemplateRenderDataProcessor templateRenderDataProcessor;

    private final PoiTemplateRenderer poiTemplateRenderer;

    @Override
    public byte[] render(TemplateRenderRequestDto request) {
        validateOutputFormat(request.outputFormat());

        TemplateProperties.TemplateConfiguration templateConfiguration =
                templateRegistry.getTemplateConfiguration(request.templateCode());

        Map<String, Object> renderData = templateRenderDataProcessor.process(request.data());

        byte[] templateContent = templateLoader.load(templateConfiguration.getPath());

        return poiTemplateRenderer.render(templateContent, renderData);
    }

    private void validateOutputFormat(DocumentFormat outputFormat) {
        if (outputFormat != DocumentFormat.DOCX) {
            throw new IllegalArgumentException("Формат '%s' пока не поддерживается".formatted(outputFormat));
        }
    }
}