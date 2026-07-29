package ru.otus.hw.finance_service.repository.projection;

import java.math.BigDecimal;

public interface CategoryStatisticsProjection {

    String getCategoryName();

    String getCategoryEmoji();

    BigDecimal getAmount();
}