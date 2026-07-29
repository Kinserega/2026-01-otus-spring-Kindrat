package ru.otus.hw.finance_service.telegram.handler.message;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.otus.hw.finance_service.telegram.keyboard.CategoryManagementKeyboardFactory;
import ru.otus.hw.finance_service.telegram.keyboard.Constants;
import ru.otus.hw.finance_service.telegram.service.TelegramMessageService;

@Component
@RequiredArgsConstructor
public class CategoriesHandler implements MessageHandler {

    private final CategoryManagementKeyboardFactory categoryManagementKeyboardFactory;
    
    private final TelegramMessageService telegramMessageService;

    @Override
    public boolean supports(String messageText) {
        return Constants.CATEGORIES_BUTTON.equals(messageText);
    }

    @Override
    public void handle(Update update) {
        SendMessage message = SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text("Выберите тип категорий:")
                .replyMarkup(categoryManagementKeyboardFactory.createTypeSelection())
                .build();

        telegramMessageService.sendMessage(message);
    }
}