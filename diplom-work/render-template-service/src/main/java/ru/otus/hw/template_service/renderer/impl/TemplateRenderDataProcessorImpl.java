package ru.otus.hw.template_service.renderer.impl;

import org.springframework.stereotype.Component;
import ru.otus.hw.template_service.renderer.TemplateRenderDataProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class TemplateRenderDataProcessorImpl implements TemplateRenderDataProcessor {

    @Override
    public Map<String, Object> process(Map<String, Object> sourceData) {
        if (sourceData == null || sourceData.isEmpty()) {
            return new HashMap<>();
        }
        Map<String, Object> processedData = new HashMap<>();
        sourceData.forEach((key, value) -> processedData.put(key, normalizeValue(value)));
        return processedData;
    }

    private Object normalizeValue(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof List<?> values) {
            return normalizeList(values);
        }
        if (value instanceof Map<?, ?> values) {
            return normalizeMap(values);
        }
        return value;
    }

    private List<Object> normalizeList(List<?> sourceValues) {
        return sourceValues.stream()
                .map(this::normalizeValue)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    private Map<String, Object> normalizeMap(Map<?, ?> sourceValues) {
        Map<String, Object> normalizedValues = new HashMap<>();

        sourceValues.forEach((key, value) ->
                normalizedValues.put(
                        String.valueOf(key),
                        normalizeValue(value)
                )
        );
        return normalizedValues;
    }
}