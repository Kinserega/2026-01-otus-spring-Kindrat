package ru.otus.hw.finance_service.telegram.keyboard;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.otus.hw.finance_service.dto.category.CategoryResponseDto;

import java.util.Collections;
import java.util.List;

@Component
public class BudgetKeyboardFactory {

    public InlineKeyboardMarkup createCategorySelection(List<CategoryResponseDto> categories) {
        List<InlineKeyboardRow> keyboard = categories.stream()
                .map(this::createCategoryRow)
                .toList();

        return InlineKeyboardMarkup.builder()
                .keyboard(keyboard)
                .build();
    }

    private InlineKeyboardRow createCategoryRow(CategoryResponseDto category) {
        InlineKeyboardButton button = InlineKeyboardButton.builder()
                .text("%s %s".formatted(category.emoji(), category.name()))
                .callbackData(Constants.BUDGET_CATEGORY_CALLBACK_PREFIX + category.id())
                .build();

        return new InlineKeyboardRow(button);
    }

    public InlineKeyboardMarkup createManagementMenu() {
        InlineKeyboardButton currentBudgetsButton = InlineKeyboardButton.builder()
                .text("📋 Мои бюджеты")
                .callbackData(Constants.CURRENT_BUDGETS_CALLBACK)
                .build();

        InlineKeyboardButton setBudgetButton = InlineKeyboardButton.builder()
                .text("➕ Установить бюджет")
                .callbackData(Constants.SET_BUDGET_CALLBACK)
                .build();

        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        new InlineKeyboardRow(currentBudgetsButton),
                        new InlineKeyboardRow(setBudgetButton)
                ))
                .build();
    }

    public InlineKeyboardMarkup createBudgetActions(Long budgetId) {
        InlineKeyboardButton editButton = InlineKeyboardButton.builder()
                .text("✏️ Изменить")
                .callbackData(Constants.EDIT_BUDGET_CALLBACK_PREFIX + budgetId)
                .build();

        InlineKeyboardButton deleteButton = InlineKeyboardButton.builder()
                .text("🗑 Удалить")
                .callbackData(Constants.DELETE_BUDGET_CALLBACK_PREFIX + budgetId)
                .build();

        return InlineKeyboardMarkup.builder()
                .keyboard(Collections.singleton(new InlineKeyboardRow(editButton, deleteButton)))
                .build();
    }
}