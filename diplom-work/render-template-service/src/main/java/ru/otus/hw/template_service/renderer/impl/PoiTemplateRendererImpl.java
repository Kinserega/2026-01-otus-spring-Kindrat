package ru.otus.hw.template_service.renderer.impl;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.otus.hw.template_service.exception.TemplateRenderingException;
import ru.otus.hw.template_service.renderer.PoiTemplateRenderer;
import ru.otus.hw.template_service.renderer.TemplateConfigurationFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PoiTemplateRendererImpl implements PoiTemplateRenderer {

    private final TemplateConfigurationFactory templateConfigurationFactory;

    @Override
    public byte[] render(byte[] templateContent, Map<String, Object> renderData) {
        validateTemplateContent(templateContent);
        Map<String, Object> safeRenderData = resolveRenderData(renderData);
        Configure configuration = templateConfigurationFactory.create();
        try (
                InputStream inputStream = new ByteArrayInputStream(templateContent);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                XWPFTemplate template = XWPFTemplate
                        .compile(inputStream, configuration)
                        .render(safeRenderData)
        ) {
            template.write(outputStream);
            byte[] documentContent = outputStream.toByteArray();

            return documentContent;
        } catch (Exception exception) {
            log.error("Ошибка формирования DOCX-документа по шаблону", exception);
            throw new TemplateRenderingException("Не удалось сформировать DOCX-документ", exception);
        }
    }

    private void validateTemplateContent(byte[] templateContent) {
        if (templateContent == null  || templateContent.length == 0) {
            throw new IllegalArgumentException("Входной поток DOCX-шаблона не должен быть null");
        }
    }

    private Map<String, Object> resolveRenderData(Map<String, Object> renderData) {
        if (renderData == null) {
            return Collections.emptyMap();
        }
        return renderData;
    }
}