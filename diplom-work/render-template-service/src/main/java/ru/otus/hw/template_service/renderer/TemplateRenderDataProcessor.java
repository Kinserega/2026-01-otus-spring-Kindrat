package ru.otus.hw.template_service.renderer;

import java.util.Map;

public interface TemplateRenderDataProcessor {

    Map<String, Object> process(Map<String, Object> sourceData);
}