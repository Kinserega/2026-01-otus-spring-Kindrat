package ru.otus.hw.finance_service.telegram.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.otus.hw.finance_service.exception.TelegramMessageSendingException;
import ru.otus.hw.finance_service.telegram.service.TelegramMessageService;

@Service
@RequiredArgsConstructor
public class TelegramMessageServiceImpl implements TelegramMessageService {

    private final TelegramClient telegramClient;

    @Override
    public void sendMessage(SendMessage message) {
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException exception) {
            throw new TelegramMessageSendingException("Не удалось отправить сообщение пользователю Telegram", exception);
        }
    }

    @Override
    public void sendDocument(SendDocument document) {
        try {
            telegramClient.execute(document);
        } catch (TelegramApiException exception) {
            throw new TelegramMessageSendingException("Не удалось отправить документ пользователю Telegram", exception);
        }
    }

    @Override
    public void answerCallbackQuery(AnswerCallbackQuery answerCallbackQuery) {
        try {
            telegramClient.execute(answerCallbackQuery);
        } catch (TelegramApiException exception) {
            throw new TelegramMessageSendingException("Не удалось обработать нажатие Telegram-кнопки", exception);
        }
    }
}