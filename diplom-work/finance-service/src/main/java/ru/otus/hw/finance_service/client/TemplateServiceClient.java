package ru.otus.hw.finance_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.otus.hw.finance_service.dto.template.TemplateRenderRequestDto;

@FeignClient(
        name = "template-service",
        url = "${integration.template-service.url}"
)
public interface TemplateServiceClient {

    @PostMapping(value = "/api/templates/render", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<byte[]> render(@RequestBody TemplateRenderRequestDto request);
}