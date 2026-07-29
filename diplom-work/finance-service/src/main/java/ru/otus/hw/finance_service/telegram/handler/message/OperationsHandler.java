package ru.otus.hw.finance_service.telegram.handler.message;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
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
public class OperationsHandler implements MessageHandler {

    private static final int FIRST_PAGE = 0;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private final TelegramUserService telegramUserService;

    private final FinanceOperationService financeOperationService;

    private final TelegramMessageService telegramMessageService;

    private final OperationKeyboardFactory operationKeyboardFactory;

    private final OperationsKeyboardFactory operationsPageKeyboardFactory;

    @Override
    public boolean supports(String messageText) {
        return Constants.OPERATIONS_BUTTON.equals(messageText);
    }

    @Override
    public void handle(Update update) {
        UserResponseDto user =
                telegramUserService.getOrCreateUser(update.getMessage().getFrom());

        Page<FinanceOperationResponseDto> operations =
                financeOperationService.findRecentOperations(user.id(), FIRST_PAGE);

        Long chatId = update.getMessage().getChatId();

        if (operations.isEmpty()) {
            sendEmptyOperationsMessage(chatId);
            return;
        }

        sendOperationsHeader(chatId);
        operations.forEach(operation -> sendOperationMessage(chatId, operation));
        sendPaginationMessage(chatId, operations);
    }

    private void sendEmptyOperationsMessage(Long chatId) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("У вас пока нет финансовых операций.")
                .build();

        telegramMessageService.sendMessage(message);
    }

    private void sendOperationsHeader(Long chatId) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("📋 Последние операции:")
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

    private void sendPaginationMessage(Long chatId, Page<FinanceOperationResponseDto> operations) {
        if (!operations.hasNext()) {
            return;
        }
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("Показать следующие операции?")
                .replyMarkup(operationsPageKeyboardFactory.create(operations))
                .build();
        telegramMessageService.sendMessage(message);
    }

    private String buildOperationMessage(
            FinanceOperationResponseDto operation
    ) {
        String operationSymbol =
                operation.operationType() == FinanceOperationType.EXPENSE
                        ? "➖"
                        : "➕";

        String operationType =
                operation.operationType() == FinanceOperationType.EXPENSE
                        ? "— расход"
                        : "— доход";

        return """
                %s %s %s ₽
                %s %s
                %s
                """.formatted(
                operationSymbol,
                operation.categoryEmoji(),
                operation.amount(),
                operation.categoryName(),
                operationType,
                operation.operationDate().format(DATE_FORMATTER)
        );
    }
}