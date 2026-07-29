package ru.otus.hw.template_service.exception;

public class TemplateRenderingException extends RuntimeException {

    public TemplateRenderingException(String message, Throwable cause) {
        super(message, cause);
    }
}