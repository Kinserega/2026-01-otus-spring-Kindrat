package ru.otus.hw.finance_service.telegram.handler.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.otus.hw.finance_service.dto.auth.UserResponseDto;
import ru.otus.hw.finance_service.service.CategoryService;
import ru.otus.hw.finance_service.service.TelegramUserService;
import ru.otus.hw.finance_service.telegram.keyboard.MainMenuKeyboardFactory;
import ru.otus.hw.finance_service.telegram.service.TelegramMessageService;

@Component
@RequiredArgsConstructor
public class StartCommandHandler implements CommandHandler {

    private static final String START_COMMAND = "/start";
    
    private final TelegramUserService telegramUserService;
    
    private final CategoryService categoryService;
    
    private final MainMenuKeyboardFactory mainMenuKeyboardFactory;
    
    private final TelegramMessageService telegramMessageService;

    @Override
    public boolean supports(String command) {
        return START_COMMAND.equals(command);
    }

    @Override
    public void handle(Update update) {
        UserResponseDto user = telegramUserService.getOrCreateUser(update.getMessage().getFrom());

        categoryService.initializeDefaultCategories(user.id());

        SendMessage message = SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text("""
                        Добро пожаловать в Personal Budget Assistant Bot!

                        Выберите необходимое действие.
                        """)
                .replyMarkup(mainMenuKeyboardFactory.create())
                .build();

        telegramMessageService.sendMessage(message);
    }
}