package ru.otus.hw.finance_service.telegram.bot;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.otus.hw.finance_service.config.TelegramProperties;
import ru.otus.hw.finance_service.telegram.dispatcher.TelegramUpdateDispatcher;

@Component
@RequiredArgsConstructor
public class PersonalBudgetTelegramBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final TelegramProperties telegramProperties;

    private final TelegramUpdateDispatcher telegramUpdateDispatcher;

    @Override
    public String getBotToken() {
        return telegramProperties.token();
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        telegramUpdateDispatcher.dispatch(update);
    }
}