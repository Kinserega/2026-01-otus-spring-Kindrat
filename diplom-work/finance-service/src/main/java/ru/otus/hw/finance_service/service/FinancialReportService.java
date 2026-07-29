package ru.otus.hw.finance_service.service;

import org.telegram.telegrambots.meta.api.objects.User;
import ru.otus.hw.finance_service.dto.template.GeneratedDocumentDto;
import ru.otus.hw.finance_service.enums.DocumentFormat;
import ru.otus.hw.finance_service.enums.StatisticsPeriod;

public interface FinancialReportService {

    GeneratedDocumentDto generate(
            User telegramUser,
            StatisticsPeriod statisticsPeriod,
            DocumentFormat documentFormat
    );
}