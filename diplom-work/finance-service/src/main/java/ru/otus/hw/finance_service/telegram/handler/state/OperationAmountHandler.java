package ru.otus.hw.finance_service.telegram.handler.state;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.otus.hw.finance_service.dto.auth.UserResponseDto;
import ru.otus.hw.finance_service.entity.UserBotState;
import ru.otus.hw.finance_service.enums.BotState;
import ru.otus.hw.finance_service.enums.FinanceOperationType;
import ru.otus.hw.finance_service.service.FinanceOperationService;
import ru.otus.hw.finance_service.service.TelegramUserService;
import ru.otus.hw.finance_service.service.UserBotStateService;
import ru.otus.hw.finance_service.telegram.handler.state.StateHandler;
import ru.otus.hw.finance_service.telegram.keyboard.MainMenuKeyboardFactory;
import ru.otus.hw.finance_service.telegram.service.TelegramMessageService;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class OperationAmountHandler implements StateHandler {
    
    private final TelegramUserService telegramUserService;
    
    private final UserBotStateService userBotStateService;

    private final FinanceOperationService financeOperationService;
    
    private final MainMenuKeyboardFactory mainMenuKeyboardFactory;

    private final TelegramMessageService telegramMessageService;
    
    @Override
    public boolean supports(UserBotState userBotState) {
        return userBotState.getState() == BotState.WAITING_OPERATION_AMOUNT;
    }

    @Override
    public void handle(Update update, UserBotState userBotState) {
        UserResponseDto user = telegramUserService.getOrCreateUser(
                update.getMessage().getFrom());

        BigDecimal amount = parseAmount(update.getMessage().getText());
        if (amount == null) {
            sendInvalidAmountMessage(update.getMessage().getChatId());
            return;
        }
        financeOperationService.createOperation(
                user.id(),
                userBotState.getSelectedCategoryId(),
                amount,
                userBotState.getOperationType()
        );
        userBotStateService.reset(user.id());
        sendOperationCreatedMessage(update.getMessage().getChatId(), amount, userBotState.getOperationType());
    }

    private BigDecimal parseAmount(String value) {
        try {
            BigDecimal amount = new BigDecimal(value.trim().replace(",", "."));
            return amount.signum() > 0 ? amount : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    
    private void sendOperationCreatedMessage(
            Long chatId,
            BigDecimal amount,
            FinanceOperationType operationType
    ) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(buildSuccessMessage(amount, operationType))
                .replyMarkup(mainMenuKeyboardFactory.create())
                .build();
        telegramMessageService.sendMessage(message);
    }

    
    private String buildSuccessMessage(BigDecimal amount, FinanceOperationType operationType) {
        return operationType == FinanceOperationType.EXPENSE
                ? "✅ Расход %s ₽ успешно сохранён.".formatted(amount)
                : "✅ Доход %s ₽ успешно сохранён.".formatted(amount);
    }

    
    private void sendInvalidAmountMessage(Long chatId) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("""
                        Некорректная сумма.
                        Введите положительное число, например:
                        450
                        450.50
                        """)
                .build();
        telegramMessageService.sendMessage(message);
    }
}