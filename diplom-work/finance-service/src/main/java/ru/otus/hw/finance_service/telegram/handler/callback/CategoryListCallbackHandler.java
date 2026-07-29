package ru.otus.hw.finance_service.telegram.handler.callback;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.otus.hw.finance_service.dto.auth.UserResponseDto;
import ru.otus.hw.finance_service.dto.category.CategoryResponseDto;
import ru.otus.hw.finance_service.enums.FinanceOperationType;
import ru.otus.hw.finance_service.service.CategoryService;
import ru.otus.hw.finance_service.service.TelegramUserService;
import ru.otus.hw.finance_service.telegram.keyboard.CategoryManagementKeyboardFactory;
import ru.otus.hw.finance_service.telegram.keyboard.Constants;
import ru.otus.hw.finance_service.telegram.service.TelegramMessageService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryListCallbackHandler implements CallbackHandler {

    private final TelegramUserService telegramUserService;

    private final CategoryService categoryService;

    private final CategoryManagementKeyboardFactory categoryManagementKeyboardFactory;

    private final TelegramMessageService telegramMessageService;

    @Override
    public boolean supports(String callbackData) {
        return Constants.EXPENSE_CATEGORIES_CALLBACK.equals(callbackData)
                || Constants.INCOME_CATEGORIES_CALLBACK.equals(callbackData);
    }

    @Override
    public void handle(Update update) {
        FinanceOperationType operationType = resolveOperationType(
                update.getCallbackQuery().getData()
        );

        UserResponseDto user = telegramUserService.getOrCreateUser(
                update.getCallbackQuery().getFrom()
        );

        List<CategoryResponseDto> categories = categoryService.findActiveCategories(
                user.id(),
                operationType
        );

        SendMessage message = SendMessage.builder()
                .chatId(update.getCallbackQuery().getMessage().getChatId())
                .text(buildMessageText(operationType))
                .replyMarkup(categoryManagementKeyboardFactory.createCategories(
                        categories,
                        operationType
                ))
                .build();

        telegramMessageService.sendMessage(message);
    }

    private FinanceOperationType resolveOperationType(String callbackData) {
        return Constants.EXPENSE_CATEGORIES_CALLBACK.equals(callbackData)
                ? FinanceOperationType.EXPENSE
                : FinanceOperationType.INCOME;
    }

    private String buildMessageText(FinanceOperationType operationType) {
        return operationType == FinanceOperationType.EXPENSE
                ? "➖ Категории расходов:"
                : "➕ Категории доходов:";
    }
}