package ru.otus.hw.finance_service.telegram.keyboard;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.Arrays;

@Component
public class StatisticsKeyboardFactory {

    public InlineKeyboardMarkup create() {
        InlineKeyboardButton todayButton = InlineKeyboardButton.builder()
                .text("📅 Сегодня")
                .callbackData(Constants.TODAY_STATISTICS_CALLBACK)
                .build();

        InlineKeyboardButton currentMonthButton = InlineKeyboardButton.builder()
                .text("🗓 Текущий месяц")
                .callbackData(Constants.CURRENT_MONTH_STATISTICS_CALLBACK)
                .build();

        return InlineKeyboardMarkup.builder()
                .keyboard((Arrays.asList(
                        new InlineKeyboardRow(todayButton),
                        new InlineKeyboardRow(currentMonthButton)
                )))
                .build();
    }
}