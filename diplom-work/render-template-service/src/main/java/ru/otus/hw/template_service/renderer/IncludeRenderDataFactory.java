package ru.otus.hw.template_service.renderer;

import com.deepoove.poi.data.DocxRenderData;

public interface IncludeRenderDataFactory {

    DocxRenderData create(byte[] documentContent);
}