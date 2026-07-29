package ru.otus.hw.finance_service.telegram.handler.callback;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.otus.hw.finance_service.dto.template.GeneratedDocumentDto;
import ru.otus.hw.finance_service.enums.DocumentFormat;
import ru.otus.hw.finance_service.enums.StatisticsPeriod;
import ru.otus.hw.finance_service.service.FinancialReportService;
import ru.otus.hw.finance_service.telegram.keyboard.Constants;
import ru.otus.hw.finance_service.telegram.service.TelegramMessageService;

import java.io.ByteArrayInputStream;

@Component
@RequiredArgsConstructor
public class GenerateDocxReportCallbackHandler implements CallbackHandler {

    private final FinancialReportService financialReportService;

    private final TelegramMessageService telegramMessageService;

    @Override
    public boolean supports(String callbackData) {
        return callbackData.startsWith(Constants.DOCX_REPORT_CALLBACK_PREFIX);
    }

    @Override
    public void handle(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        StatisticsPeriod statisticsPeriod = extractStatisticsPeriod(update.getCallbackQuery().getData());

        sendGenerationStartedMessage(chatId);

        GeneratedDocumentDto document = financialReportService.generate(
                update.getCallbackQuery().getFrom(),
                statisticsPeriod,
                DocumentFormat.DOCX
        );

        sendDocument(chatId, document);
    }

    private StatisticsPeriod extractStatisticsPeriod(String callbackData) {
        String statisticsPeriodName = callbackData.substring(Constants.DOCX_REPORT_CALLBACK_PREFIX.length());

        return StatisticsPeriod.valueOf(statisticsPeriodName);
    }

    private void sendGenerationStartedMessage(Long chatId) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("⏳ Формирую финансовый отчёт...")
                .build();

        telegramMessageService.sendMessage(message);
    }

    private void sendDocument(Long chatId, GeneratedDocumentDto document) {
        ByteArrayInputStream documentInputStream = new ByteArrayInputStream(document.content());
        InputFile inputFile = new InputFile(documentInputStream, document.fileName());
        SendDocument sendDocument = SendDocument.builder()
                .chatId(chatId)
                .document(inputFile)
                .caption("📄 Финансовый отчёт успешно сформирован.")
                .build();
        telegramMessageService.sendDocument(sendDocument);
    }
}