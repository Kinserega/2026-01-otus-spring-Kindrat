package ru.otus.hw.finance_service.telegram.handler.message;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface MessageHandler {

    boolean supports(String messageText);

    void handle(Update update);
}