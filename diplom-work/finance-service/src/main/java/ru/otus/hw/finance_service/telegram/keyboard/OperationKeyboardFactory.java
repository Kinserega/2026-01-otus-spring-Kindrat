package ru.otus.hw.finance_service.telegram.keyboard;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.Collections;

@Component
public class OperationKeyboardFactory {

    public InlineKeyboardMarkup create(Long operationId) {
        InlineKeyboardButton editButton = InlineKeyboardButton.builder()
                .text("✏️ Изменить")
                .callbackData(Constants.EDIT_OPERATION_CALLBACK_PREFIX + operationId)
                .build();

        InlineKeyboardButton deleteButton = InlineKeyboardButton.builder()
                .text("🗑 Удалить")
                .callbackData(Constants.DELETE_OPERATION_CALLBACK_PREFIX + operationId)
                .build();

        return InlineKeyboardMarkup.builder()
                .keyboard(Collections.singleton(new InlineKeyboardRow(editButton, deleteButton)))
                .build();
    }
}