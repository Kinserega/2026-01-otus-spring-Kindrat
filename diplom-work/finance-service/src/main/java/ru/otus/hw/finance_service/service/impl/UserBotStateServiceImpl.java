package ru.otus.hw.finance_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.finance_service.entity.UserBotState;
import ru.otus.hw.finance_service.enums.BotState;
import ru.otus.hw.finance_service.enums.FinanceOperationType;
import ru.otus.hw.finance_service.repository.UserBotStateRepository;
import ru.otus.hw.finance_service.service.UserBotStateService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserBotStateServiceImpl implements UserBotStateService {

    private final UserBotStateRepository userBotStateRepository;

    @Override
    @Transactional
    public void waitOperationAmount(Long userId, Long categoryId,
                                    FinanceOperationType operationType) {
        UserBotState userBotState = userBotStateRepository.findByUserId(userId)
                .orElseGet(UserBotState::new);

        userBotState.setUserId(userId);
        userBotState.setState(BotState.WAITING_OPERATION_AMOUNT);
        userBotState.setSelectedCategoryId(categoryId);
        userBotState.setOperationType(operationType);

        userBotStateRepository.save(userBotState);
    }

    @Override
    @Transactional
    public void waitOperationUpdateAmount(Long userId, Long operationId) {
        UserBotState userBotState = userBotStateRepository.findByUserId(userId)
                .orElseGet(UserBotState::new);
        userBotState.setUserId(userId);
        userBotState.setState(BotState.WAITING_OPERATION_UPDATE_AMOUNT);
        userBotState.setSelectedOperationId(operationId);
        userBotState.setSelectedCategoryId(null);
        userBotState.setOperationType(null);

        userBotStateRepository.save(userBotState);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserBotState> findByUserId(Long userId) {
        return userBotStateRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public void reset(Long userId) {
        userBotStateRepository.findByUserId(userId)
                .ifPresent(userBotState -> {
                    userBotState.setState(BotState.IDLE);
                    userBotState.setSelectedCategoryId(null);
                    userBotState.setSelectedOperationId(null);
                    userBotState.setOperationType(null);
                    userBotState.setTemporaryCategoryName(null);
                    userBotState.setSelectedBudgetId(null);
                });
    }

    @Override
    @Transactional
    public void waitCategoryName(Long userId, FinanceOperationType operationType) {
        UserBotState userBotState = getOrCreateState(userId);

        userBotState.setState(BotState.WAITING_CATEGORY_NAME);
        userBotState.setOperationType(operationType);
        userBotState.setTemporaryCategoryName(null);
        userBotState.setSelectedCategoryId(null);
        userBotState.setSelectedOperationId(null);
    }

    @Override
    @Transactional
    public void waitCategoryEmoji(
            Long userId,
            String categoryName
    ) {
        UserBotState userBotState = getOrCreateState(userId);

        userBotState.setTemporaryCategoryName(categoryName);
        userBotState.setState(BotState.WAITING_CATEGORY_EMOJI);
    }

    private UserBotState getOrCreateState(Long userId) {
        return userBotStateRepository.findByUserId(userId)
                .orElseGet(() -> createState(userId));
    }

    private UserBotState createState(Long userId) {
        UserBotState userBotState = new UserBotState();
        userBotState.setUserId(userId);
        userBotState.setState(BotState.IDLE);

        return userBotStateRepository.save(userBotState);
    }

    @Override
    @Transactional
    public void waitCategoryRename(Long userId, Long categoryId) {
        UserBotState userBotState = getOrCreateState(userId);

        userBotState.setState(BotState.WAITING_CATEGORY_RENAME);
        userBotState.setSelectedCategoryId(categoryId);
        userBotState.setSelectedOperationId(null);
        userBotState.setOperationType(null);
        userBotState.setTemporaryCategoryName(null);
    }

    @Override
    @Transactional
    public void waitBudgetAmount(Long userId, Long categoryId) {
        UserBotState userBotState = getOrCreateState(userId);

        userBotState.setState(BotState.WAITING_BUDGET_AMOUNT);
        userBotState.setSelectedCategoryId(categoryId);
        userBotState.setSelectedOperationId(null);
        userBotState.setOperationType(null);
        userBotState.setTemporaryCategoryName(null);
    }

    @Override
    @Transactional
    public void waitBudgetUpdateAmount(Long userId, Long budgetId) {
        UserBotState userBotState = getOrCreateState(userId);

        userBotState.setState(BotState.WAITING_BUDGET_UPDATE_AMOUNT);
        userBotState.setSelectedBudgetId(budgetId);
        userBotState.setSelectedCategoryId(null);
        userBotState.setSelectedOperationId(null);
        userBotState.setOperationType(null);
        userBotState.setTemporaryCategoryName(null);
    }
}