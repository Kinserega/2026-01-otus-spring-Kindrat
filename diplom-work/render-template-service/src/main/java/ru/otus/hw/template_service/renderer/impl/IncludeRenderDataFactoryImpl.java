package ru.otus.hw.template_service.renderer.impl;

import com.deepoove.poi.data.DocxRenderData;
import com.deepoove.poi.data.Includes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.template_service.renderer.DocxContentValidator;
import ru.otus.hw.template_service.renderer.IncludeRenderDataFactory;

@Component
@RequiredArgsConstructor
public class IncludeRenderDataFactoryImpl implements IncludeRenderDataFactory {

    private final DocxContentValidator docxContentValidator;

    @Override
    public DocxRenderData create(byte[] documentContent) {
        if (documentContent == null || documentContent.length == 0) {
            return null;
        }
        if (!docxContentValidator.isDocx(documentContent)) {
            throw new IllegalArgumentException("Включаемый документ не является корректным DOCX-файлом");
        }
        return Includes.ofBytes(documentContent).create();
    }
}