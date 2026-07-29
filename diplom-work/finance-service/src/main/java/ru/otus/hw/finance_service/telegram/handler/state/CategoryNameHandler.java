package ru.otus.hw.finance_service.telegram.handler.state;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.otus.hw.finance_service.entity.UserBotState;
import ru.otus.hw.finance_service.enums.BotState;
import ru.otus.hw.finance_service.service.UserBotStateService;
import ru.otus.hw.finance_service.telegram.service.TelegramMessageService;

@Component
@RequiredArgsConstructor
public class CategoryNameHandler implements StateHandler {

    private static final int MAX_CATEGORY_NAME_LENGTH = 100;

    private final UserBotStateService userBotStateService;

    private final TelegramMessageService telegramMessageService;
    
    @Override
    public boolean supports(UserBotState userBotState) {
        return userBotState.getState() == BotState.WAITING_CATEGORY_NAME;
    }

    @Override
    public void handle(Update update, UserBotState userBotState) {
        String categoryName = update.getMessage().getText().trim();
        if (!isValidCategoryName(categoryName)) {
            sendInvalidCategoryNameMessage(update.getMessage().getChatId());
            return;
        }
        userBotStateService.waitCategoryEmoji(userBotState.getUserId(), categoryName);
        SendMessage message = SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text("""
                        Введите emoji категории.
                        Например: ☕
                        """)
                .build();
        telegramMessageService.sendMessage(message);
    }

    private boolean isValidCategoryName(String categoryName) {
        return !categoryName.isBlank()
                && categoryName.length() <= MAX_CATEGORY_NAME_LENGTH;
    }
    
    private void sendInvalidCategoryNameMessage(Long chatId) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("Название категории должно содержать от 1 до 100 символов.")
                .build();

        telegramMessageService.sendMessage(message);
    }
}