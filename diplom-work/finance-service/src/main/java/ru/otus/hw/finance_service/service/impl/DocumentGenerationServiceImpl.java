package ru.otus.hw.finance_service.service.impl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.otus.hw.finance_service.client.TemplateServiceClient;
import ru.otus.hw.finance_service.dto.template.GeneratedDocumentDto;
import ru.otus.hw.finance_service.dto.template.TemplateRenderRequestDto;
import ru.otus.hw.finance_service.exception.DocumentGenerationException;
import ru.otus.hw.finance_service.service.DocumentGenerationService;

@Service
@RequiredArgsConstructor
public class DocumentGenerationServiceImpl implements DocumentGenerationService {

    private static final String DEFAULT_FILE_NAME = "generated-document.docx";

    private final TemplateServiceClient templateServiceClient;

    @Override
    @CircuitBreaker(name = "templateServiceBreaker", fallbackMethod = "generateFallback")
    public GeneratedDocumentDto generate(TemplateRenderRequestDto request) {
        try {
            ResponseEntity<byte[]> response = templateServiceClient.render(request);

            byte[] documentContent = validateAndGetContent(response);

            return new GeneratedDocumentDto(
                    resolveFileName(response.getHeaders()),
                    resolveContentType(response.getHeaders()),
                    documentContent);
        } catch (DocumentGenerationException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new DocumentGenerationException("Не удалось сформировать отчётный документ", exception);
        }
    }

    private GeneratedDocumentDto generateFallback(TemplateRenderRequestDto request, Throwable throwable) {
        throw new DocumentGenerationException("Сервис формирования документов временно недоступен", throwable);
    }

    private byte[] validateAndGetContent(ResponseEntity<byte[]> response) {
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new DocumentGenerationException("Template-service вернул ошибку: %s".formatted(response.getStatusCode()));
        }

        byte[] documentContent = response.getBody();
        if (documentContent.length == 0) {
            throw new DocumentGenerationException("Template-service вернул пустой документ");
        }

        return documentContent;
    }

    private String resolveFileName(HttpHeaders headers) {
        ContentDisposition contentDisposition =
                headers.getContentDisposition();
        String fileName = contentDisposition.getFilename();
        return fileName.isBlank() ? DEFAULT_FILE_NAME : fileName;
    }

    private String resolveContentType(HttpHeaders headers) {
        MediaType contentType = headers.getContentType();
        return contentType.toString();
    }
}