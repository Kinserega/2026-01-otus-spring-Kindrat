package ru.otus.hw.template_service.renderer;

import java.io.InputStream;

public interface TemplateLoader {

    byte[] load(String templatePath);
}