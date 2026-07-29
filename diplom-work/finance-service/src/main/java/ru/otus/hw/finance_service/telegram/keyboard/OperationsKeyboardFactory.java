package ru.otus.hw.finance_service.telegram.keyboard;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;

@Component
public class OperationsKeyboardFactory {

    public InlineKeyboardMarkup create(Page<?> page) {
        if (!page.hasNext()) {
            return null;
        }

        InlineKeyboardButton showMoreButton =
                InlineKeyboardButton.builder()
                        .text("Показать ещё")
                        .callbackData(Constants.OPERATIONS_PAGE + (page.getNumber() + 1))
                        .build();

        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(new InlineKeyboardRow(showMoreButton)))
                .build();
    }
}