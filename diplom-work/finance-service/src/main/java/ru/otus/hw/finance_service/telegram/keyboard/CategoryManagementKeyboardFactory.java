package ru.otus.hw.finance_service.telegram.keyboard;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.otus.hw.finance_service.dto.category.CategoryResponseDto;
import ru.otus.hw.finance_service.enums.FinanceOperationType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class CategoryManagementKeyboardFactory {

    public InlineKeyboardMarkup createTypeSelection() {
        InlineKeyboardButton expenseButton = InlineKeyboardButton.builder()
                .text("➖ Расходы")
                .callbackData(Constants.EXPENSE_CATEGORIES_CALLBACK)
                .build();

        InlineKeyboardButton incomeButton = InlineKeyboardButton.builder()
                .text("➕ Доходы")
                .callbackData(Constants.INCOME_CATEGORIES_CALLBACK)
                .build();

        return InlineKeyboardMarkup.builder()
                .keyboard(Collections.singleton(new InlineKeyboardRow(expenseButton, incomeButton)))
                .build();
    }

    public InlineKeyboardMarkup createCategories(
            List<CategoryResponseDto> categories,
            FinanceOperationType operationType
    ) {
        List<InlineKeyboardRow> keyboard = new ArrayList<>(
                categories.stream()
                        .map(this::createCategoryRow)
                        .toList()
        );

        keyboard.add(createAddCategoryRow(operationType));

        return InlineKeyboardMarkup.builder()
                .keyboard(keyboard)
                .build();
    }

    private InlineKeyboardRow createCategoryRow(CategoryResponseDto category) {
        InlineKeyboardButton categoryButton = InlineKeyboardButton.builder()
                .text("%s %s".formatted(category.emoji(), category.name()))
                .callbackData(Constants.MANAGE_CATEGORY_CALLBACK_PREFIX + category.id())
                .build();

        return new InlineKeyboardRow(categoryButton);
    }

    private InlineKeyboardRow createAddCategoryRow(
            FinanceOperationType operationType
    ) {
        InlineKeyboardButton addCategoryButton = InlineKeyboardButton.builder()
                .text("➕ Добавить категорию")
                .callbackData(Constants.ADD_CATEGORY_CALLBACK_PREFIX + operationType.name())
                .build();

        return new InlineKeyboardRow(addCategoryButton);
    }

    public InlineKeyboardMarkup createCategoryManagement(
            CategoryResponseDto category
    ) {
        if (category.systemCategory()) {
            return createSystemCategoryManagement();
        }

        return createUserCategoryManagement(category.id());
    }

    private InlineKeyboardMarkup createUserCategoryManagement(Long categoryId) {
        InlineKeyboardButton renameButton = InlineKeyboardButton.builder()
                .text("✏️ Переименовать")
                .callbackData(Constants.RENAME_CATEGORY_CALLBACK_PREFIX + categoryId)
                .build();

        InlineKeyboardButton deleteButton = InlineKeyboardButton.builder()
                .text("🗑 Удалить")
                .callbackData(Constants.DELETE_CATEGORY_CALLBACK_PREFIX + categoryId)
                .build();

        return InlineKeyboardMarkup.builder()
                .keyboard(Collections.singleton(new InlineKeyboardRow(renameButton, deleteButton)))
                .build();
    }

    private InlineKeyboardMarkup createSystemCategoryManagement() {
        InlineKeyboardButton informationButton = InlineKeyboardButton.builder()
                .text("🔒 Системная категория")
                .callbackData("SYSTEM_CATEGORY_INFO")
                .build();

        return InlineKeyboardMarkup.builder()
                .keyboard(Collections.singleton(
                        new InlineKeyboardRow(informationButton)
                ))
                .build();
    }
}