package ru.otus.hw.finance_service.telegram.handler.state;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.otus.hw.finance_service.entity.UserBotState;
import ru.otus.hw.finance_service.enums.BotState;
import ru.otus.hw.finance_service.service.CategoryService;
import ru.otus.hw.finance_service.service.UserBotStateService;
import ru.otus.hw.finance_service.telegram.keyboard.MainMenuKeyboardFactory;
import ru.otus.hw.finance_service.telegram.service.TelegramMessageService;

@Component
@RequiredArgsConstructor
public class CategoryEmojiHandler implements StateHandler {

    private static final int MAX_EMOJI_LENGTH = 20;
    
    private final CategoryService categoryService;

    private final UserBotStateService userBotStateService;

    private final MainMenuKeyboardFactory mainMenuKeyboardFactory;
    private final TelegramMessageService telegramMessageService;

    
    @Override
    public boolean supports(UserBotState userBotState) {
        return userBotState.getState() == BotState.WAITING_CATEGORY_EMOJI;
    }

    @Override
    public void handle(Update update, UserBotState userBotState) {
        String emoji = update.getMessage().getText().trim();
        if (!isValidEmoji(emoji)) {
            sendInvalidEmojiMessage(update.getMessage().getChatId());
            return;
        }
        categoryService.createCategory(
                userBotState.getUserId(),
                userBotState.getTemporaryCategoryName(),
                emoji,
                userBotState.getOperationType()
        );
        userBotStateService.reset(userBotState.getUserId());
        SendMessage message = SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text("✅ Категория успешно создана.")
                .replyMarkup(mainMenuKeyboardFactory.create())
                .build();

        telegramMessageService.sendMessage(message);
    }

    private boolean isValidEmoji(String emoji) {
        return !emoji.isBlank()
                && emoji.length() <= MAX_EMOJI_LENGTH;
    }
    
    private void sendInvalidEmojiMessage(Long chatId) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("Введите emoji категории, например: ☕")
                .build();
        telegramMessageService.sendMessage(message);
    }
}