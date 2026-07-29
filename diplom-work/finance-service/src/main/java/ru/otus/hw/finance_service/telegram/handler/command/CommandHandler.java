package ru.otus.hw.finance_service.telegram.handler.command;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface CommandHandler {
    
    boolean supports(String command);

    void handle(Update update);
}