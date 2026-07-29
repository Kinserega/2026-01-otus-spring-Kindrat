package ru.otus.hw.finance_service.telegram.handler.callback;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.otus.hw.finance_service.dto.auth.UserResponseDto;
import ru.otus.hw.finance_service.dto.category.CategoryResponseDto;
import ru.otus.hw.finance_service.service.CategoryService;
import ru.otus.hw.finance_service.service.TelegramUserService;
import ru.otus.hw.finance_service.telegram.keyboard.CategoryManagementKeyboardFactory;
import ru.otus.hw.finance_service.telegram.keyboard.Constants;
import ru.otus.hw.finance_service.telegram.service.TelegramMessageService;

@Component
@RequiredArgsConstructor
public class ManageCategoryCallbackHandler implements CallbackHandler {

    private final TelegramUserService telegramUserService;

    private final CategoryService categoryService;

    private final CategoryManagementKeyboardFactory categoryManagementKeyboardFactory;

    private final TelegramMessageService telegramMessageService;

    @Override
    public boolean supports(String callbackData) {
        return callbackData.startsWith(
                Constants.MANAGE_CATEGORY_CALLBACK_PREFIX
        );
    }

    @Override
    public void handle(Update update) {
        UserResponseDto user = telegramUserService.getOrCreateUser(update.getCallbackQuery().getFrom());
        Long categoryId = extractCategoryId(update.getCallbackQuery().getData());

        CategoryResponseDto category = categoryService.findById(user.id(), categoryId);

        SendMessage message = SendMessage.builder()
                .chatId(update.getCallbackQuery().getMessage().getChatId())
                .text("""
                        Категория: %s %s

                        Выберите действие:
                        """.formatted(
                        category.emoji(),
                        category.name()
                ))
                .replyMarkup(
                        categoryManagementKeyboardFactory.createCategoryManagement(category)
                )
                .build();

        telegramMessageService.sendMessage(message);
    }

    private Long extractCategoryId(String callbackData) {
        return Long.valueOf(callbackData.substring(Constants.MANAGE_CATEGORY_CALLBACK_PREFIX.length()));
    }
}