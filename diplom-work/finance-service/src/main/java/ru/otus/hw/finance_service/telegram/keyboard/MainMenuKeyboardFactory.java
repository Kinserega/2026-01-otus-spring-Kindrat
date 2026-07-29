package ru.otus.hw.finance_service.telegram.keyboard;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

@Component
public class MainMenuKeyboardFactory {

    public ReplyKeyboardMarkup create() {
        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(
                        createRow(Constants.ADD_EXPENSE_BUTTON, Constants.ADD_INCOME_BUTTON),
                        createRow(Constants.STATISTICS_BUTTON, Constants.REPORT_BUTTON),
                        createRow(Constants.CATEGORIES_BUTTON, Constants.OPERATIONS_BUTTON),
                        createSingleButtonRow(Constants.BUDGETS_BUTTON)
                ))
                .resizeKeyboard(true)
                .selective(true)
                .build();
    }

    private KeyboardRow createSingleButtonRow(String buttonText) {
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(new KeyboardButton(buttonText));
        return keyboardRow;
    }

    private KeyboardRow createRow(String firstButtonText, String secondButtonText) {
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(new KeyboardButton(firstButtonText));
        keyboardRow.add(new KeyboardButton(secondButtonText));
        return keyboardRow;
    }
}