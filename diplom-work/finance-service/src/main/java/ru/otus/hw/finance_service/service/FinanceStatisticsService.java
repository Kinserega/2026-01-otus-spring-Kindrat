package ru.otus.hw.finance_service.service;

import ru.otus.hw.finance_service.dto.statistics.FinanceStatisticsDto;
import ru.otus.hw.finance_service.enums.StatisticsPeriod;

public interface FinanceStatisticsService {

    FinanceStatisticsDto getStatistics(
            Long userId,
            StatisticsPeriod statisticsPeriod
    );
}