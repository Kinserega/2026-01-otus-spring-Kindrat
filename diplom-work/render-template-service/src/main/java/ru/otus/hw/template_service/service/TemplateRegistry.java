package ru.otus.hw.template_service.service;

import ru.otus.hw.template_service.config.TemplateProperties;

public interface TemplateRegistry {

    TemplateProperties.TemplateConfiguration getTemplateConfiguration(String templateCode);
}