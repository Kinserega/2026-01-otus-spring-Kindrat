package ru.otus.hw.template_service.renderer;

import java.io.InputStream;
import java.util.Map;

public interface PoiTemplateRenderer {

    byte[] render(byte[] templateContent, Map<String, Object> renderData);
}