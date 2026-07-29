package ru.otus.hw.finance_service.telegram.handler.callback;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.otus.hw.finance_service.dto.auth.UserResponseDto;
import ru.otus.hw.finance_service.service.CategoryService;
import ru.otus.hw.finance_service.service.TelegramUserService;
import ru.otus.hw.finance_service.telegram.keyboard.Constants;
import ru.otus.hw.finance_service.telegram.keyboard.MainMenuKeyboardFactory;
import ru.otus.hw.finance_service.telegram.service.TelegramMessageService;

@Component
@RequiredArgsConstructor
public class DeleteCategoryCallbackHandler implements CallbackHandler {

    private final TelegramUserService telegramUserService;

    private final CategoryService categoryService;

    private final MainMenuKeyboardFactory mainMenuKeyboardFactory;

    private final TelegramMessageService telegramMessageService;

    @Override
    public boolean supports(String callbackData) {
        return callbackData.startsWith(Constants.DELETE_CATEGORY_CALLBACK_PREFIX);
    }

    @Override
    public void handle(Update update) {
        UserResponseDto user = telegramUserService.getOrCreateUser(update.getCallbackQuery().getFrom());

        Long categoryId = extractCategoryId(update.getCallbackQuery().getData());

        categoryService.deleteCategory(user.id(), categoryId);

        SendMessage message = SendMessage.builder()
                .chatId(update.getCallbackQuery().getMessage().getChatId())
                .text("🗑 Категория успешно удалена.")
                .replyMarkup(mainMenuKeyboardFactory.create())
                .build();

        telegramMessageService.sendMessage(message);
    }

    private Long extractCategoryId(String callbackData) {
        return Long.valueOf(callbackData.substring(Constants.DELETE_CATEGORY_CALLBACK_PREFIX.length()));
    }
}