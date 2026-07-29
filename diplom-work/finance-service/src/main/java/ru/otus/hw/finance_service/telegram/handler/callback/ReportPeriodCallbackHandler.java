package ru.otus.hw.finance_service.telegram.handler.callback;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.otus.hw.finance_service.enums.StatisticsPeriod;
import ru.otus.hw.finance_service.telegram.keyboard.Constants;
import ru.otus.hw.finance_service.telegram.keyboard.ReportKeyboardFactory;
import ru.otus.hw.finance_service.telegram.service.TelegramMessageService;

@Component
@RequiredArgsConstructor
public class ReportPeriodCallbackHandler implements CallbackHandler {

    private final ReportKeyboardFactory reportKeyboardFactory;

    private final TelegramMessageService telegramMessageService;

    @Override
    public boolean supports(String callbackData) {
        return Constants.TODAY_REPORT_PERIOD_CALLBACK.equals(callbackData)
                || Constants.CURRENT_MONTH_REPORT_PERIOD_CALLBACK.equals(callbackData);
    }

    @Override
    public void handle(Update update) {
        StatisticsPeriod statisticsPeriod = resolveStatisticsPeriod(update.getCallbackQuery().getData());
        SendMessage message = SendMessage.builder()
                .chatId(update.getCallbackQuery().getMessage().getChatId())
                .text("""
                        Выбран период: %s

                        Выберите формат отчёта:
                        """.formatted(resolvePeriodName(statisticsPeriod)))
                .replyMarkup(
                        reportKeyboardFactory.createFormatSelection(
                                statisticsPeriod.name()
                        )
                )
                .build();
        telegramMessageService.sendMessage(message);
    }

    private StatisticsPeriod resolveStatisticsPeriod(String callbackData) {
        return Constants.TODAY_REPORT_PERIOD_CALLBACK.equals(callbackData)
                ? StatisticsPeriod.TODAY
                : StatisticsPeriod.CURRENT_MONTH;
    }

    private String resolvePeriodName(StatisticsPeriod statisticsPeriod) {
        return statisticsPeriod == StatisticsPeriod.TODAY
                ? "текущий день"
                : "текущий месяц";
    }
}