package ru.otus.hw.finance_service.telegram.keyboard;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.Arrays;
import java.util.Collections;

@Component
public class ReportKeyboardFactory {

    public InlineKeyboardMarkup createPeriodSelection() {
        InlineKeyboardButton todayButton = InlineKeyboardButton.builder()
                .text("📅 Сегодня")
                .callbackData(Constants.TODAY_REPORT_PERIOD_CALLBACK)
                .build();

        InlineKeyboardButton currentMonthButton = InlineKeyboardButton.builder()
                .text("🗓 Текущий месяц")
                .callbackData(Constants.CURRENT_MONTH_REPORT_PERIOD_CALLBACK)
                .build();

        return InlineKeyboardMarkup.builder()
                .keyboard(Arrays.asList(
                        new InlineKeyboardRow(todayButton),
                        new InlineKeyboardRow(currentMonthButton)
                ))
                .build();
    }

    public InlineKeyboardMarkup createFormatSelection(String statisticsPeriodName) {
        InlineKeyboardButton docxButton = InlineKeyboardButton.builder()
                .text("📄 Word")
                .callbackData(Constants.DOCX_REPORT_CALLBACK_PREFIX + statisticsPeriodName)
                .build();

        return InlineKeyboardMarkup.builder()
                .keyboard(Collections.singleton(new InlineKeyboardRow(docxButton)))
                .build();
    }
}