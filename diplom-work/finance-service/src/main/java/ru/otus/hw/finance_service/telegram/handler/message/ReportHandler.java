package ru.otus.hw.finance_service.telegram.handler.message;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.otus.hw.finance_service.telegram.keyboard.Constants;
import ru.otus.hw.finance_service.telegram.keyboard.MainMenuKeyboardFactory;
import ru.otus.hw.finance_service.telegram.keyboard.ReportKeyboardFactory;
import ru.otus.hw.finance_service.telegram.service.TelegramMessageService;

@Component
@RequiredArgsConstructor
public class ReportHandler implements MessageHandler {

    private final ReportKeyboardFactory reportKeyboardFactory;
    
    private final TelegramMessageService telegramMessageService;

    @Override
    public boolean supports(String messageText) {
        return Constants.REPORT_BUTTON.equals(messageText);
    }

    @Override
    public void handle(Update update) {
        SendMessage message = SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text("Выберите период финансового отчёта:")
                .replyMarkup(reportKeyboardFactory.createPeriodSelection())
                .build();
        telegramMessageService.sendMessage(message);
    }
}