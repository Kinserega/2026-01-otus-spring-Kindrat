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
import ru.otus.hw.finance_service.telegram.keyboard.BudgetKeyboardFactory;
import ru.otus.hw.finance_service.telegram.keyboard.Constants;
import ru.otus.hw.finance_service.telegram.service.TelegramMessageService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SetBudgetCallbackHandler implements CallbackHandler {

    private final TelegramUserService telegramUserService;
    
    private final CategoryService categoryService;
    
    private final BudgetKeyboardFactory budgetKeyboardFactory;
    
    private final TelegramMessageService telegramMessageService;

    @Override
    public boolean supports(String callbackData) {
        return Constants.SET_BUDGET_CALLBACK.equals(callbackData);
    }

    @Override
    public void handle(Update update) {
        UserResponseDto user = telegramUserService.getOrCreateUser(update.getCallbackQuery().getFrom());
        List<CategoryResponseDto> categories = categoryService.findActiveCategories(
                user.id(),
                FinanceOperationType.EXPENSE
        );
        SendMessage message = SendMessage.builder()
                .chatId(update.getCallbackQuery().getMessage().getChatId())
                .text("Выберите категорию расходов:")
                .replyMarkup(budgetKeyboardFactory.createCategorySelection(categories))
                .build();

        telegramMessageService.sendMessage(message);
    }
}