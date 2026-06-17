package ru.otus.hw.domain;

import java.math.BigDecimal;

public record CustomerOrder(
        Long id,
        String customerName,
        String productName,
        int quantity,
        BigDecimal totalAmount,
        String deliveryAddress
) {
}
