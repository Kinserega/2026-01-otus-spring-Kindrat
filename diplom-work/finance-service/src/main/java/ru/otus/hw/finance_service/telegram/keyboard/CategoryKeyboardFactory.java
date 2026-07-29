package ru.otus.hw.finance_service.telegram.keyboard;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.otus.hw.finance_service.dto.category.CategoryResponseDto;
import ru.otus.hw.finance_service.enums.FinanceOperationType;

import java.util.List;

@Component
public class CategoryKeyboardFactory {

    public InlineKeyboardMarkup create(
            List<CategoryResponseDto> categories,
            FinanceOperationType operationType
    ) {
        List<InlineKeyboardRow> keyboard = categories.stream()
                .map(category -> createCategoryRow(category, operationType))
                .toList();

        return InlineKeyboardMarkup.builder()
                .keyboard(keyboard)
                .build();
    }

    private InlineKeyboardRow createCategoryRow(
            CategoryResponseDto category,
            FinanceOperationType operationType
    ) {
        InlineKeyboardButton button = InlineKeyboardButton.builder()
                .text(buildButtonText(category))
                .callbackData(buildCallbackData(category.id(), operationType))
                .build();

        return new InlineKeyboardRow(button);
    }

    private String buildCallbackData(
            Long categoryId,
            FinanceOperationType operationType
    ) {
        return "%s%s:%d".formatted(
                Constants.CATEGORY_CALLBACK_PREFIX,
                operationType.name(),
                categoryId
        );
    }

    private String buildButtonText(CategoryResponseDto category) {
        return "%s %s".formatted(category.emoji(), category.name());
    }
}