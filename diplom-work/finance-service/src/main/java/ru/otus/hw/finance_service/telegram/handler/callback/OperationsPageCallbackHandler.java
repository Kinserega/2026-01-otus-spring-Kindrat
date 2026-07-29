package ru.otus.hw.finance_service.telegram.handler.callback;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.otus.hw.finance_service.dto.auth.UserResponseDto;
import ru.otus.hw.finance_service.dto.operation.FinanceOperationResponseDto;
import ru.otus.hw.finance_service.enums.FinanceOperationType;
import ru.otus.hw.finance_service.service.FinanceOperationService;
import ru.otus.hw.finance_service.service.TelegramUserService;
import ru.otus.hw.finance_service.telegram.keyboard.Constants;
import ru.otus.hw.finance_service.telegram.keyboard.OperationKeyboardFactory;
import ru.otus.hw.finance_service.telegram.keyboard.OperationsKeyboardFactory;
import ru.otus.hw.finance_service.telegram.service.TelegramMessageService;

import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class OperationsPageCallbackHandler implements CallbackHandler {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private final TelegramUserService telegramUserService;

    private final FinanceOperationService financeOperationService;

    private final TelegramMessageService telegramMessageService;

    private final OperationKeyboardFactory operationKeyboardFactory;

    private final OperationsKeyboardFactory operationsPageKeyboardFactory;

    @Override
    public boolean supports(String callbackData) {
        return callbackData != null && callbackData.startsWith(Constants.OPERATIONS_PAGE);
    }

    @Override
    public void handle(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        int page = extractPage(callbackData);

        User telegramUser = update.getCallbackQuery().getFrom();
        UserResponseDto user =
                telegramUserService.getOrCreateUser(telegramUser);

        Page<FinanceOperationResponseDto> operations =
                financeOperationService.findRecentOperations(user.id(), page);

        Long chatId = update.getCallbackQuery()
                .getMessage()
                .getChatId();

        answerCallback(update);

        if (operations.isEmpty()) {
            sendNoMoreOperationsMessage(chatId);
            return;
        }
        operations.forEach(operation -> sendOperationMessage(chatId, operation));
        sendPaginationMessage(chatId, operations);
    }

    private int extractPage(String callbackData) {
        String pageValue = callbackData.substring(
                Constants.OPERATIONS_PAGE.length()
        );

        try {
            int page = Integer.parseInt(pageValue);

            if (page < 0) {
                throw new IllegalArgumentException("Номер страницы не может быть отрицательным");
            }

            return page;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Некорректный номер страницы: " + pageValue, exception);
        }
    }

    private void answerCallback(Update update) {
        AnswerCallbackQuery answer = AnswerCallbackQuery.builder()
                .callbackQueryId(update.getCallbackQuery().getId())
                .build();

        telegramMessageService.answerCallbackQuery(answer);
    }

    private void sendNoMoreOperationsMessage(Long chatId) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("Больше операций нет.")
                .build();

        telegramMessageService.sendMessage(message);
    }

    private void sendOperationMessage(
            Long chatId,
            FinanceOperationResponseDto operation
    ) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(buildOperationMessage(operation))
                .replyMarkup(operationKeyboardFactory.create(operation.id()))
                .build();

        telegramMessageService.sendMessage(message);
    }

    private void sendPaginationMessage(
            Long chatId,
            Page<FinanceOperationResponseDto> operations
    ) {
        if (!operations.hasNext()) {
            return;
        }

        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("Есть ещё операции:")
                .replyMarkup(operationsPageKeyboardFactory.create(operations))
                .build();

        telegramMessageService.sendMessage(message);
    }

    private String buildOperationMessage(FinanceOperationResponseDto operation) {
        boolean expense =
                operation.operationType() == FinanceOperationType.EXPENSE;

        return """
                %s %s %s ₽
                %s %s
                %s
                """.formatted(
                expense ? "➖" : "➕",
                operation.categoryEmoji(),
                operation.amount(),
                operation.categoryName(),
                expense ? "— расход" : "— доход",
                operation.operationDate().format(DATE_FORMATTER)
        );
    }
}