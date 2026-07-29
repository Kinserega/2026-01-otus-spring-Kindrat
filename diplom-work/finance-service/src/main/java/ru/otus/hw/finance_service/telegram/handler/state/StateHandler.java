package ru.otus.hw.finance_service.telegram.handler.state;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.otus.hw.finance_service.entity.UserBotState;

public interface StateHandler {

    boolean supports(UserBotState userBotState);

    void handle(Update update, UserBotState userBotState);
}