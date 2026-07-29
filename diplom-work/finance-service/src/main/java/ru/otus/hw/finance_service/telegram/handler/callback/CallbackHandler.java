package ru.otus.hw.finance_service.telegram.handler.callback;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface CallbackHandler {

    boolean supports(String callbackData);

    void handle(Update update);
}