package ru.otus.hw.finance_service.telegram.handler.message;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.otus.hw.finance_service.dto.auth.UserResponseDto;
import ru.otus.hw.finance_service.dto.category.CategoryResponseDto;
import ru.otus.hw.finance_service.enums.FinanceOperationType;
import ru.otus.hw.finance_service.service.CategoryService;
import ru.otus.hw.finance_service.service.TelegramUserService;
import ru.otus.hw.finance_service.telegram.handler.message.MessageHandler;
import ru.otus.hw.finance_service.telegram.keyboard.CategoryKeyboardFactory;
import ru.otus.hw.finance_service.telegram.keyboard.Constants;
import ru.otus.hw.finance_service.telegram.keyboard.MainMenuKeyboardFactory;
import ru.otus.hw.finance_service.telegram.service.TelegramMessageService;

import java.util.List;


@Component
@RequiredArgsConstructor
public class OperationStartHandler implements MessageHandler {

    private final TelegramUserService telegramUserService;

    private final CategoryService categoryService;

    private final CategoryKeyboardFactory categoryKeyboardFactory;
    
    private final TelegramMessageService telegramMessageService;

    @Override
    public boolean supports(String messageText) {
        return Constants.ADD_EXPENSE_BUTTON.equals(messageText)
                || Constants.ADD_INCOME_BUTTON.equals(messageText);
    }

    @Override
    public void handle(Update update) {
        FinanceOperationType operationType = resolveOperationType(update.getMessage().getText());
        UserResponseDto user = telegramUserService.getOrCreateUser(update.getMessage().getFrom());
        List<CategoryResponseDto> categories = categoryService.findActiveCategories(
                user.id(),
                operationType
        );
        SendMessage message = SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text(buildMessageText(operationType))
                .replyMarkup(categoryKeyboardFactory.create(categories, operationType))
                .build();
        telegramMessageService.sendMessage(message);
    }

    private FinanceOperationType resolveOperationType(String messageText) {
        return Constants.ADD_EXPENSE_BUTTON.equals(messageText)
                ? FinanceOperationType.EXPENSE
                : FinanceOperationType.INCOME;
    }

    private String buildMessageText(FinanceOperationType operationType) {
        return operationType == FinanceOperationType.EXPENSE
                ? "Выберите категорию расхода:"
                : "Выберите категорию дохода:";
    }
}