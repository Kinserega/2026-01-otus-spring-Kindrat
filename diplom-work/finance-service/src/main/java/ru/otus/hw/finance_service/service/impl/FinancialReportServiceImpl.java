package ru.otus.hw.finance_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.otus.hw.finance_service.constants.TemplateCodeConstants;
import ru.otus.hw.finance_service.dto.auth.UserResponseDto;
import ru.otus.hw.finance_service.dto.budget.BudgetResponseDto;
import ru.otus.hw.finance_service.dto.operation.FinanceOperationResponseDto;
import ru.otus.hw.finance_service.dto.statistics.FinanceStatisticsDto;
import ru.otus.hw.finance_service.dto.template.GeneratedDocumentDto;
import ru.otus.hw.finance_service.dto.template.TemplateRenderRequestDto;
import ru.otus.hw.finance_service.enums.DocumentFormat;
import ru.otus.hw.finance_service.enums.StatisticsPeriod;
import ru.otus.hw.finance_service.service.BudgetService;
import ru.otus.hw.finance_service.service.DocumentGenerationService;
import ru.otus.hw.finance_service.service.FinanceOperationService;
import ru.otus.hw.finance_service.service.FinanceStatisticsService;
import ru.otus.hw.finance_service.service.FinancialReportDataFactory;
import ru.otus.hw.finance_service.service.FinancialReportService;
import ru.otus.hw.finance_service.service.TelegramUserService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FinancialReportServiceImpl implements FinancialReportService {

    private final TelegramUserService telegramUserService;

    private final FinanceStatisticsService financeStatisticsService;

    private final FinanceOperationService financeOperationService;

    private final BudgetService budgetService;

    private final FinancialReportDataFactory financialReportDataFactory;

    private final DocumentGenerationService documentGenerationService;

    @Override
    public GeneratedDocumentDto generate(
            User telegramUser,
            StatisticsPeriod statisticsPeriod,
            DocumentFormat documentFormat
    ) {
        UserResponseDto user = telegramUserService.getOrCreateUser(
                telegramUser
        );

        FinanceStatisticsDto statistics =
                financeStatisticsService.getStatistics(
                        user.id(),
                        statisticsPeriod
                );

        List<FinanceOperationResponseDto> operations =
                financeOperationService.findOperationsByPeriod(
                        user.id(),
                        statistics.periodFrom(),
                        statistics.periodTo()
                );

        List<BudgetResponseDto> budgets = resolveBudgets(
                user.id(),
                statisticsPeriod
        );

        Map<String, Object> renderData =
                financialReportDataFactory.create(
                        user,
                        statistics,
                        operations,
                        budgets,
                        statisticsPeriod
                );

        TemplateRenderRequestDto templateRequest =
                new TemplateRenderRequestDto(
                        TemplateCodeConstants.FINANCIAL_REPORT,
                        documentFormat,
                        renderData
                );

        return documentGenerationService.generate(templateRequest);
    }

    private List<BudgetResponseDto> resolveBudgets(
            Long userId,
            StatisticsPeriod statisticsPeriod
    ) {
        if (statisticsPeriod != StatisticsPeriod.CURRENT_MONTH) {
            return Collections.emptyList();
        }

        return budgetService.findCurrentMonthBudgets(userId);
    }
}