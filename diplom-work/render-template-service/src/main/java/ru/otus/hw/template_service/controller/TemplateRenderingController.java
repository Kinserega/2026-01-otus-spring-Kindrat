package ru.otus.hw.template_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.template_service.dto.TemplateRenderRequestDto;
import ru.otus.hw.template_service.enums.DocumentFormat;
import ru.otus.hw.template_service.service.TemplateRegistry;
import ru.otus.hw.template_service.service.TemplateRenderingService;
import ru.otus.hw.template_service.config.TemplateProperties;
import ru.otus.hw.template_service.service.TemplateRegistry;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
public class TemplateRenderingController {

    private static final String DOCX_EXTENSION = ".docx";

    private static final MediaType DOCX_MEDIA_TYPE = MediaType.parseMediaType(
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    private final TemplateRegistry templateRegistry;

    private final TemplateRenderingService templateRenderingService;

    @PostMapping("/render")
    public ResponseEntity<byte[]> render(@Valid @RequestBody TemplateRenderRequestDto request) {
        byte[] documentContent = templateRenderingService.render(request);
        String fileName = buildFileName(request);
        return ResponseEntity.ok()
                .contentType(DOCX_MEDIA_TYPE)
                .contentLength(documentContent.length)
                .header(HttpHeaders.CONTENT_DISPOSITION, buildContentDisposition(fileName))
                .body(documentContent);
    }

    private String buildFileName(TemplateRenderRequestDto request) {
        TemplateProperties.TemplateConfiguration configuration = templateRegistry.getTemplateConfiguration(request.templateCode());
        String configuredFileName = configuration.getFileName();
        String baseFileName = configuredFileName == null
                || configuredFileName.isBlank()
                ? request.templateCode()
                : configuredFileName;

        return baseFileName + DOCX_EXTENSION;
    }

    private String buildContentDisposition(String fileName) {
        return ContentDisposition.attachment()
                .filename(fileName, StandardCharsets.UTF_8)
                .build()
                .toString();
    }
}