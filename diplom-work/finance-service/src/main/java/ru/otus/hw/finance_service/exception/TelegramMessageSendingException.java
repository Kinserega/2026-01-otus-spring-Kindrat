package ru.otus.hw.finance_service.exception;

public class TelegramMessageSendingException extends RuntimeException {

    public TelegramMessageSendingException(String message, Throwable cause) {
        super(message, cause);
    }
}