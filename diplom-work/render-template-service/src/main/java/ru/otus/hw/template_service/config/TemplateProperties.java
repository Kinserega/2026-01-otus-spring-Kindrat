package ru.otus.hw.template_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.otus.hw.template_service.enums.DocumentFormat;

import java.util.HashMap;
import java.util.Map;


@Getter
@Setter
@ConfigurationProperties(prefix = "template")
public class TemplateProperties {


    private DocumentFormat defaultFormat = DocumentFormat.DOCX;

    private Map<String, TemplateConfiguration> templates = new HashMap<>();

    @Getter
    @Setter
    public static class TemplateConfiguration {

        private String path;

        private String fileName;

        private boolean enabled = true;
    }
}