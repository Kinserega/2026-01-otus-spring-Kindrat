package ru.otus.hw.finance_service.dto.template;

public record GeneratedDocumentDto(
        String fileName,
        String contentType,
        byte[] content
) {
}