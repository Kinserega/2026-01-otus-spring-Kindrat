package ru.otus.hw.template_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.template_service.config.TemplateProperties;
import ru.otus.hw.template_service.exception.TemplateNotFoundException;
import ru.otus.hw.template_service.service.TemplateRegistry;

@Component
@RequiredArgsConstructor
public class TemplateRegistryImpl implements TemplateRegistry {

    private final TemplateProperties templateProperties;

    @Override
    public TemplateProperties.TemplateConfiguration getTemplateConfiguration(String templateCode) {
        validateTemplateCode(templateCode);
        TemplateProperties.TemplateConfiguration configuration = templateProperties.getTemplates().get(templateCode);
        if (configuration == null) {
            throw new TemplateNotFoundException("Шаблон с кодом '%s' не зарегистрирован".formatted(templateCode));
        }
        validateTemplateEnabled(templateCode, configuration);
        validateTemplatePath(templateCode, configuration);
        return configuration;
    }

    private void validateTemplateCode(String templateCode) {
        if (templateCode == null || templateCode.isBlank()) {
            throw new IllegalArgumentException("Код шаблона не должен быть пустым");
        }
    }

    private void validateTemplateEnabled(
            String templateCode,
            TemplateProperties.TemplateConfiguration configuration
    ) {
        if (!configuration.isEnabled()) {
            throw new IllegalArgumentException("Шаблон '%s' отключён".formatted(templateCode));
        }
    }

    private void validateTemplatePath(
            String templateCode,
            TemplateProperties.TemplateConfiguration configuration
    ) {
        if (configuration.getPath() == null || configuration.getPath().isBlank()) {
            throw new IllegalArgumentException("Для шаблона '%s' не указан путь".formatted(templateCode));
        }
    }
}