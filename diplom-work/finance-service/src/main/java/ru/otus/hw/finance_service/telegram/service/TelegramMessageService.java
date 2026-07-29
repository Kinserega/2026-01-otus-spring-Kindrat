package ru.otus.hw.finance_service.telegram.service;

import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface TelegramMessageService {

    void sendMessage(SendMessage message);

    void sendDocument(SendDocument document);

    void answerCallbackQuery(AnswerCallbackQuery answerCallbackQuery);
}