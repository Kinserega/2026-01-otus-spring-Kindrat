package ru.otus.hw.template_service.renderer.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import ru.otus.hw.template_service.exception.TemplateLoadingException;
import ru.otus.hw.template_service.renderer.TemplateLoader;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClasspathTemplateLoader implements TemplateLoader {

    private final ResourceLoader resourceLoader;

    @Override
    @Cacheable(cacheNames = "documentTemplates", key = "#templatePath", sync = true)
    public byte[] load(String templatePath) {
        validateTemplatePath(templatePath);
        Resource templateResource = resourceLoader.getResource(templatePath);
        if (!templateResource.exists()) {
            throw new TemplateLoadingException("Шаблон документа не найден: %s".formatted(templatePath));
        }

        try {
            return templateResource.getContentAsByteArray();
        } catch (IOException exception) {
            throw new TemplateLoadingException("Не удалось прочитать шаблон документа: %s".formatted(templatePath), exception);
        }
    }

    private void validateTemplatePath(String templatePath) {
        if (templatePath == null || templatePath.isBlank()) {
            throw new IllegalArgumentException("Путь к шаблону документа не должен быть пустым");
        }
    }
}